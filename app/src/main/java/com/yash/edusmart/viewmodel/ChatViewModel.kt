package com.yash.edusmart.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yash.edusmart.api.ChatEntity
import com.yash.edusmart.api.MainAppApi
import com.yash.edusmart.data.AssignmentDTO
import com.yash.edusmart.data.ChatMessage
import com.yash.edusmart.db.Assignments
import com.yash.edusmart.db.ChatEntries
import com.yash.edusmart.repository.AssignmentLocalRepo
import com.yash.edusmart.repository.ChatLocalDbRepo
import com.yash.edusmart.repository.ContextRepo
import com.yash.edusmart.services.SocketService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatLocalDbRepo: ChatLocalDbRepo,
    private val contextRepo: ContextRepo,
    private val assignmentLocalRepo: AssignmentLocalRepo,
    private val mainAppApi: MainAppApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    // ✅ Toast Event
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()


    @Volatile private var isConnected = false
    @Volatile private var activeGroupId: String? = null

    @Volatile private var privateSubscribed = false
    @Volatile private var groupSubscribedFor: String? = null
    @Volatile private var assignmentSubscribedFor: String? = null
    @Volatile private var socketConnected = false




    fun showToast(message: String) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            _toastEvent.emit(message)
        }
    }

    val isLoggedIn: StateFlow<Boolean?> =
        contextRepo.getLoggedin()      // Flow<Boolean?>
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val userType: StateFlow<String?> =
        contextRepo.getUserType()      // Flow<Boolean?>
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val email: StateFlow<String?> =
        contextRepo.getEmail()        // Flow<Boolean?>
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)


    init {
        viewModelScope.launch {
            combine(userType, isLoggedIn, email) { type, logged, em ->
                Triple(type, logged, em)
            }
                .distinctUntilChanged()
                .collect { (type, logged, em) ->
                    if (logged != true) return@collect
                    if (type.isNullOrBlank() || em.isNullOrBlank()) return@collect

                    if (type == "STUDENT") {
                        val branch = contextRepo.getBranch().filterNotNull().first()
                        val sem = contextRepo.getSemester().filterNotNull().first()
                        _uiState.update { it.copy(branch = branch, sem = sem) }
                        start("$branch $sem")
                    } else if (type == "TEACHER") {
                        startTeacher(em)   // ✅ pass String email
                    }
                }
        }
    }



    val messages = chatLocalDbRepo.getMessages()
        .map { list -> list.sortedByDescending { it.timeStamp } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    suspend fun startTeacher(email: String) {
        val token = contextRepo.getToken().firstOrNull().orEmpty()
        if (token.isBlank()) return

        if (!socketConnected) {
            SocketService.connect(token)
            socketConnected = true
        }
        isConnected = true


        ensurePrivateSubscribed() // ✅ only one place
    }

    private fun ensurePrivateSubscribed() {
        if (privateSubscribed) return
        privateSubscribed = true

        SocketService.subscribePrivate { msg ->
            try {
                syncPrivateHistory(msg.sender)
                val entry = ChatEntries(
                    message = msg.message,
                    isSent = false,
                    sender = msg.sender,
                    receiver = msg.receiver,
                    timeStamp = System.currentTimeMillis()
                )
                addMessage(entry)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "private msg handling failed", e)
            }
        }
    }
    private fun ensureGroupSubscribed(groupId: String, myEmail: String) {
        if (groupSubscribedFor == groupId) return
        groupSubscribedFor = groupId

        SocketService.subscribeGroup(groupId = groupId
        , onMessage = {  msg ->
            if (msg.sender == myEmail) return@subscribeGroup
            addMessage(
                ChatEntries(
                    message = msg.message,
                    isSent = false,
                    sender = msg.sender,
                    receiver = msg.receiver,
                    timeStamp = System.currentTimeMillis()
                )
            )
        }
        )
    }
    private fun ensureAssignmentSubscribed(groupId: String, myEmail: String) {
        if (assignmentSubscribedFor == groupId) return
        assignmentSubscribedFor = groupId

        SocketService.subscribeAssignment(groupId = groupId,
            onMessage = { msg ->
            if (msg.sender == myEmail) return@subscribeAssignment
            val parts = groupId.split(" ")
            addAssignment(
                Assignments(
                    enrollCom = emptyList(),
                    task = msg.task,
                    deadline = msg.deadline,
                    branch = parts.getOrNull(0) ?: "",
                    sem = parts.getOrNull(1) ?: "",
                    isCompleted = false
                )
            )
        })
    }



    suspend fun start(groupId: String) {
        if (isConnected && activeGroupId == groupId) return

        syncGroupHistory()

        val token = contextRepo.getToken().filterNotNull().first()
        if (token.isBlank()) return

        val myEmail = contextRepo.getEmail().filterNotNull().first()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (!socketConnected) {
                    SocketService.connect(token)
                    socketConnected = true
                }
                delay(300)

                isConnected = true
                activeGroupId = groupId

                ensurePrivateSubscribed()
                ensureGroupSubscribed(groupId, myEmail)
                ensureAssignmentSubscribed(groupId, myEmail)

            } catch (e: Exception) {
                Log.e("ChatViewModel", "start failed", e)
                showToast(e.message ?: "Socket connection failed")
            }
        }
    }


    fun sendPrivate(message: ChatMessage) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                SocketService.sendPrivate(message)
            } catch (e: Exception) {
                showToast(e.message ?: "Failed to send private message")
            }
        }
    }

    fun sendGroup(message: ChatMessage) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                SocketService.sendGroup(message = message, groupId = message.receiver)
            } catch (e: Exception) {
                showToast(e.message ?: "Failed to send group message")
            }
        }
    }

    fun sendAssignment(message: AssignmentDTO, groupId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.e("HIT","hit")
                SocketService.sendAssignment(message = message, groupId = groupId)

                withContext(Dispatchers.Main) {
                    showToast("Assignment Uploaded")
                }

                val parts = groupId.split(" ")
                assignmentLocalRepo.insert(
                    Assignments(
                        branch = parts.getOrNull(0) ?: "",
                        sem = parts.getOrNull(1) ?: "",
                        enrollCom = emptyList(),
                        task = message.task,
                        deadline = message.deadline,
                        isCompleted = false
                    )
                )
            } catch (e: Exception) {
                showToast(e.message ?: "Failed to send assignment")
            }
        }
    }

    fun addMessage(data: ChatEntries) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                mainAppApi.addMsg(ChatEntity(msg = data.message, isSent = data.isSent, sender = data.sender, receiver = data.receiver, timeStamp = data.timeStamp))
                chatLocalDbRepo.insert(data)
            } catch (e: Exception) {
                showToast(e.message ?: "Failed to save message")
            }
        }
    }

    fun addAssignment(data: Assignments) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                assignmentLocalRepo.insert(assignments = data)
            } catch (e: Exception) {
                showToast(e.message ?: "Failed to save assignment")
            }
        }
    }

    fun getAssignments() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                assignmentLocalRepo.getTasks()
                    .filterNotNull()
                    .collect { list ->
                        _uiState.update { it.copy(assignments = list) }
                    }
            } catch (e: Exception) {
                showToast(e.message ?: "Failed to load assignments")
            }
        }
    }

    fun syncGroupHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val branch = contextRepo.getBranch().filterNotNull().first()
                val sem = contextRepo.getSemester().filterNotNull().first()
                val groupId = "$branch $sem"

                val res = mainAppApi.getGroupMessages(branch, sem)
                if (!res.isSuccessful) return@launch

                val serverList = res.body().orEmpty().sortedBy { it.timeStamp }

                val serverIds = serverList.mapNotNull { it.id }
                if (serverIds.isEmpty()) return@launch  // safety: don't delete everything

                val localList = serverList.mapNotNull { m ->
                    val sid = m.id ?: return@mapNotNull null

                    ChatEntries(
                        id = sid,                  // ✅ server id as PK
                        message = m.msg,
                        isSent = false,            // group: student receives
                        sender = m.sender,
                        receiver = groupId,        // ✅ receiver key for group
                        timeStamp = m.timeStamp
                    )
                }

                chatLocalDbRepo.upsertAll(localList)
                chatLocalDbRepo.deleteNotInServer(groupId, serverIds)

            } catch (e: Exception) {
                Log.e("ChatViewModel", "syncGroupHistory failed", e)
            }
        }
    }

    fun syncPrivateHistory(otherEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val myEmail = contextRepo.getEmail().filterNotNull().first()

                val res = mainAppApi.getPrivateConversation(
                    email = myEmail,
                    receiverEmail = otherEmail
                )
                if (!res.isSuccessful) return@launch

                val serverList = res.body().orEmpty()
                val serverIds = serverList.mapNotNull { it.id }
                if (serverIds.isEmpty()) return@launch

                // 1️⃣ Upsert all messages
                val localList = serverList.mapNotNull { m ->
                    val sid = m.id ?: return@mapNotNull null
                    val mine = m.sender == myEmail

                    ChatEntries(
                        id = sid,                 // server id as PK
                        message = m.msg,
                        isSent = mine,
                        sender = m.sender,
                        receiver = m.receiver,    // NO thread key
                        timeStamp = m.timeStamp
                    )
                }

                chatLocalDbRepo.upsertAll(localList)

                // 2️⃣ Delete stale messages SENT BY ME
                chatLocalDbRepo.deleteNotInServer(
                    receiver = otherEmail,
                    serverIds = serverIds
                )

                // 3️⃣ Delete stale messages SENT BY OTHER USER
                chatLocalDbRepo.deleteNotInServer(
                    receiver = myEmail,
                    serverIds = serverIds
                )

            } catch (e: Exception) {
                Log.e("ChatViewModel", "syncPrivateHistory failed", e)
            }
        }
    }

    fun syncTeacherGroupHistory(branch: String, sem: String,email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val groupId = "$branch $sem"

                val res = mainAppApi.getGroupMessagesTeacher(branch, sem)
                if (!res.isSuccessful) return@launch

                val serverList = res.body().orEmpty().sortedBy { it.timeStamp }
                val serverIds = serverList.mapNotNull { it.id }
                if (serverIds.isEmpty()) return@launch

                val localList = serverList.mapNotNull { m ->
                    val sid = m.id ?: return@mapNotNull null
                    val mine = m.sender == email

                    ChatEntries(
                        id = sid,                  // ✅ server id as PK
                        message = m.msg,
                        isSent = mine,             // ✅ teacher SENT messages
                        sender = m.sender,
                        receiver = groupId,        // group thread key
                        timeStamp = m.timeStamp
                    )
                }

                chatLocalDbRepo.upsertAll(localList)
                chatLocalDbRepo.deleteNotInServer(groupId, serverIds)

            } catch (e: Exception) {
                Log.e("ChatViewModel", "syncTeacherGroupHistory failed", e)
            }
        }
    }

    fun syncTeacherPrivateHistory(otherEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val myEmail = contextRepo.getEmail().filterNotNull().first()

                val res = mainAppApi.getPrivateConversationTeacher(
                    email = myEmail,
                    receiverEmail = otherEmail
                )
                if (!res.isSuccessful) return@launch

                val serverList = res.body().orEmpty()
                val serverIds = serverList.mapNotNull { it.id }
                if (serverIds.isEmpty()) return@launch

                val localList = serverList.mapNotNull { m ->
                    val sid = m.id ?: return@mapNotNull null
                    val mine = m.sender == myEmail

                    ChatEntries(
                        id = sid,                 // server id as PK
                        message = m.msg,
                        isSent = mine,            // ✅ sender-based
                        sender = m.sender,
                        receiver = m.receiver,
                        timeStamp = m.timeStamp
                    )
                }

                chatLocalDbRepo.upsertAll(localList)

                // delete stale messages for both sides
                chatLocalDbRepo.deleteNotInServer(otherEmail, serverIds)
                chatLocalDbRepo.deleteNotInServer(myEmail, serverIds)

            } catch (e: Exception) {
                Log.e("ChatViewModel", "syncTeacherPrivateHistory failed", e)
            }
        }
    }








    fun formatTime(timestamp: Long): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))

    fun formatDate(timestamp: Long): String =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                SocketService.disconnect()
            } catch (_: Exception) {}
            isConnected = false
            activeGroupId = null
        }
    }

}


