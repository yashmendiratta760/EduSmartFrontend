package com.yash.edusmart.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatLocalDbRepo: ChatLocalDbRepo,
    private val contextRepo: ContextRepo,
    private val assignmentLocalRepo: AssignmentLocalRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    // ✅ Toast Event
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()


    @Volatile private var isConnected = false
    @Volatile private var activeGroupId: String? = null

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
        }
    }

    val isLoggedIn: StateFlow<Boolean> =
        contextRepo.getLoggedin()
            .map { it == true }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoggedIn
                    .filter { it }
                    .distinctUntilChanged()
                    .collect {
                        val branch = try {
                            contextRepo.getBranch().filterNotNull().first()
                        } catch (e: Exception) {
                            showToast(e.message ?: "Failed to read branch")
                            return@collect
                        }

                        val sem = try {
                            contextRepo.getSemester().filterNotNull().first()
                        } catch (e: Exception) {
                            showToast(e.message ?: "Failed to read semester")
                            return@collect
                        }

                        _uiState.update { it.copy(branch = branch, sem = sem) }

                        val groupId = "$branch $sem"
                        start(groupId)
                    }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "init error", e)
                showToast(e.message ?: "Something went wrong!")
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

    suspend fun start(groupId: String) {
        if (isConnected && activeGroupId == groupId) return

        val token = try {
            contextRepo.getToken().filterNotNull().first()
        } catch (e: Exception) {
            showToast(e.message ?: "Failed to read token")
            return
        }

        if (token.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                SocketService.connect(token)
                delay(300)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Socket connect failed", e)
                showToast(e.message ?: "Socket connection failed")
                return@launch
            }

            launch {
                val email = try {
                    contextRepo.getEmail().filterNotNull().first()
                } catch (e: Exception) {
                    showToast(e.message ?: "Failed to read email")
                    return@launch
                }

                // ✅ Group Chat subscription
                try {
                    SocketService.subscribeGroup(
                        groupId = groupId,
                        onMessage = { msg ->
                            try {
                                if (msg.sender == email) return@subscribeGroup
                                val entry = ChatEntries(
                                    message = msg.message,
                                    isSent = false,
                                    sender = msg.sender,
                                    receiver = msg.receiver,
                                    timeStamp = System.currentTimeMillis()
                                )
                                addMessage(entry)
                            } catch (e: Exception) {
                                Log.e("ChatViewModel", "group msg handling failed", e)
                            }
                        }
                    )
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "subscribeGroup failed", e)
                    showToast(e.message ?: "Failed to subscribe group chat")
                }

                // ✅ Assignment subscription
                try {
                    SocketService.subscribeAssignment(
                        groupId = groupId,
                        onMessage = { msg ->
                            try {
                                if (msg.sender == email) return@subscribeAssignment
                                val parts = groupId.split(" ")
                                val entry = Assignments(
                                    enrollCom = emptyList(),
                                    task = msg.task,
                                    deadline = msg.deadline,
                                    branch = parts.getOrNull(0) ?: "",
                                    sem = parts.getOrNull(1) ?: "",
                                    isCompleted = false
                                )
                                addAssignment(entry)
                            } catch (e: Exception) {
                                Log.e("ChatViewModel", "assignment msg handling failed", e)
                            }
                        }
                    )
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "subscribeAssignment failed", e)
                    showToast(e.message ?: "Failed to subscribe assignments")
                }

                // ✅ Private subscription
                try {
                    SocketService.subscribePrivate { msg ->
                        try {
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
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "subscribePrivate failed", e)
                    showToast(e.message ?: "Failed to subscribe private chat")
                }
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
                SocketService.sendAssignment(message = message, groupId = groupId)

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


