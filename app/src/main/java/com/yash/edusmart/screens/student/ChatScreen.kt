package com.yash.edusmart.screens.student

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yash.edusmart.R
import com.yash.edusmart.data.ChatMessage
import com.yash.edusmart.data.MessageType
import com.yash.edusmart.db.ChatEntries
import com.yash.edusmart.screens.component.CustomDropdownMenu
import com.yash.edusmart.screens.component.LoadingIcon
import com.yash.edusmart.screens.component.MessageSendBox
import com.yash.edusmart.screens.component.Messagebox
import com.yash.edusmart.viewmodel.ChatViewModel
import com.yash.edusmart.viewmodel.StudentUiState
import com.yash.edusmart.viewmodel.StudentViewModel
import com.yash.edusmart.viewmodel.TeacherUiState
import com.yash.edusmart.viewmodel.TeacherViewModel
import com.yash.edusmart.viewmodel.UserUiState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(innerPadding: PaddingValues,
               isStudent: Boolean,
               studentViewModel: StudentViewModel,
               teacherViewModel: TeacherViewModel,
               teacherUiState: TeacherUiState,
               chatViewModel: ChatViewModel,
               canNavigateBack: (Boolean)-> Unit,
               onBackClick: Boolean,
               selectedSemester: MutableState<String> = mutableStateOf(""),
               selectedBatch: MutableState<String> = mutableStateOf(""),
               isDarkTheme: Boolean = isSystemInDarkTheme(),
               selectedChatType: MutableState<String>,
               userUiState: UserUiState,
               studentUiState: StudentUiState){

    val branches by remember(teacherUiState.branch){
        derivedStateOf {
            teacherUiState.branch.distinct()
        }
    }
    var studentSelected by remember { mutableIntStateOf(0) }
    var receiver by remember { mutableStateOf("") }
    var typeMsg by remember { mutableStateOf("") }
    val myEmail by remember(userUiState.email){
        derivedStateOf {
            userUiState.email
        }
    }


    val allMessages by chatViewModel.messages.collectAsState(initial = emptyList())

    val msgs by remember(allMessages, receiver, myEmail) {
        derivedStateOf {
            if (receiver.isBlank()) emptyList()
            else allMessages.filter {
                (it.sender == receiver && it.receiver == myEmail) ||
                        (it.sender == myEmail && it.receiver == receiver)
            }
        }
    }

    val groupId = if (isStudent)
        "${userUiState.branch} ${userUiState.semester}"
    else
        "${selectedBatch.value} ${selectedSemester.value}"

    val msgsP by remember(allMessages, groupId) {
        derivedStateOf { allMessages.filter { it.receiver == groupId } }
    }



    val listState = rememberLazyListState()
    val activeSize =
        if (selectedChatType.value.contains("Group")) msgsP.size else msgs.size

    LaunchedEffect(activeSize, selectedChatType.value, studentSelected) {
        val shouldScroll =
            selectedChatType.value.contains("Group") ||
                    (selectedChatType.value == "Private Chat" && studentSelected == 1)

        if (shouldScroll && activeSize > 0) {
            listState.scrollToItem(0)   // index 0 for reverseLayout=true
        }
    }





    if(!isStudent) {
        LaunchedEffect(selectedBatch.value, selectedSemester.value) {
            if (selectedBatch.value != "Select Branch" && selectedSemester.value != "Select Semester") {
                teacherViewModel.getStudentListTeacherChat(
                    branch = selectedBatch.value,
                    semester = selectedSemester.value
                )
            }
        }
    }

    if(isStudent){
        LaunchedEffect(selectedChatType.value) {
            if(selectedChatType.value=="Private Chat") {
                studentViewModel.getStudentListStudentChat(
                    branch = userUiState.branch,
                    semester = userUiState.semester
                )
                studentViewModel.getAllTeacher(
                    userUiState.branch,
                    userUiState.semester
                )
            }
        }
    }

    LaunchedEffect(onBackClick) {
        studentSelected = 0
        receiver = ""
        canNavigateBack(false)

    }

    LaunchedEffect(selectedChatType.value) {
        studentSelected = 0
        receiver = ""
        canNavigateBack(false)
    }

    BackHandler(enabled = studentSelected == 1) {
        studentSelected = 0
        receiver = ""
        canNavigateBack(false)
    }

    DisposableEffect(Unit) {
        onDispose {
            studentSelected = 0
            receiver = ""
            canNavigateBack(false)
        }
    }





    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)

    ) {

        // ---- top controls (dropdowns) ----
        if (!isStudent) {
            Column(Modifier.fillMaxWidth()) {
                CustomDropdownMenu(
                    options = branches,
                    selectedOption = selectedBatch.value,
                    onOptionSelected = { selectedBatch.value = it }
                )
                CustomDropdownMenu(
                    options = listOf("1","2","3","4","5","6","7","8"),
                    selectedOption = selectedSemester.value,
                    onOptionSelected = { selectedSemester.value = it }
                )
            }
        }

        CustomDropdownMenu(
            options = if (isStudent)
                listOf("Group Chat(Official)", "Private Chat", "Group Chat(Student)")
            else
                listOf("Group Chat", "Private Chat"),
            selectedOption = selectedChatType.value,
            onOptionSelected = { selectedChatType.value = it }
        )

        // ---- MAIN CONTENT always takes remaining height ----
        Box(modifier = Modifier.weight(1f)) {

            when {
                // Private chat
                (!isStudent && selectedChatType.value == "Private Chat" && selectedBatch.value != "Select Branch" && selectedSemester.value!="Select Semester")
                        || (isStudent && selectedChatType.value == "Private Chat") -> {

                    if (studentSelected == 1) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            state = listState,
                            reverseLayout = true
                        ) {
                            items(msgs) { msg ->
                                Messagebox(
                                    message = msg.message,
                                    isSent = msg.isSent,
                                    id = 0,
                                    time = chatViewModel.formatTime(msg.timeStamp),
                                    viewModel = chatViewModel,
                                    name = msg.sender
                                )
                            }
                        }
                    } else {
                        val loading = if (isStudent) studentUiState.isChatListLoading else teacherUiState.isChatListLoading
                        if (loading) {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                LoadingIcon()
                            }
                        } else {
                            LazyColumn(Modifier.fillMaxWidth()) {
                                items(if (isStudent) studentUiState.studentDataChat else teacherUiState.studentDataChat) { st ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (isStudent) chatViewModel.syncPrivateHistory(st.email)
                                                else chatViewModel.syncTeacherPrivateHistory(st.email)

                                                studentSelected = 1
                                                receiver = st.email
                                                canNavigateBack(true)
                                            }
                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.img),
                                            contentDescription = "",
                                            modifier = Modifier.size(70.dp)
                                                .clip(shape = CircleShape)
                                        )
                                        Text(text = st.name)
                                    }
                                }

                                // show teacher list only for student (as you had)
                                if (isStudent) {
                                    items(studentUiState.teacher) { st ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    chatViewModel.syncPrivateHistory(st.email)
                                                    studentSelected = 1
                                                    receiver = st.email
                                                    canNavigateBack(true)
                                                }
                                        ) {
                                            Image(
                                                painter = painterResource(R.drawable.img),
                                                contentDescription = "",
                                                modifier = Modifier.size(70.dp).clip(shape = CircleShape)
                                            )
                                            Text(text = st.name)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Group chat
                (!isStudent && selectedChatType.value == "Group Chat"
                        && selectedBatch.value != "Select Branch"
                        && selectedSemester.value != "Select Semester")
                        || (isStudent && selectedChatType.value == "Group Chat(Official)") -> {
                    chatViewModel.syncTeacherGroupHistory(branch = selectedBatch.value, sem = selectedSemester.value,
                        email = userUiState.email)
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        state = listState,
                        reverseLayout = true
                    ) {
                        items(msgsP) { msg ->
                            Messagebox(
                                message = msg.message,
                                isSent = msg.isSent,
                                id = 0,
                                time = chatViewModel.formatTime(msg.timeStamp),
                                viewModel = chatViewModel,
                                name = msg.sender
                            )
                        }
                    }
                }
            }
        }

        // ---- SEND BOX pinned at bottom ----
        val canSend =
            (selectedChatType.value == "Private Chat" && studentSelected == 1) ||
                    (!isStudent && selectedChatType.value == "Group Chat") // teacher group send

        if (canSend) {
            MessageSendBox(
                value = typeMsg,
                isDarkTheme = isDarkTheme,
                onValueChange = { typeMsg = it },
                onSendClick = {
                    if(selectedChatType.value=="Private Chat") {
                        chatViewModel.sendPrivate(
                            ChatMessage(
                                sender = userUiState.email,
                                receiver = receiver,
                                message = typeMsg,
                                messageType = MessageType.CHAT
                            )
                        )
                        chatViewModel.addMessage(
                            ChatEntries(
                                message = typeMsg,
                                sender = userUiState.email,
                                receiver = receiver, isSent = true,
                                timeStamp = System.currentTimeMillis()
                            )
                        )
                    }else {

                        chatViewModel.sendGroup(
                            ChatMessage(
                                sender = userUiState.email,
                                receiver = selectedBatch.value + " " + selectedSemester.value,
                                message = typeMsg,
                                messageType = MessageType.CHAT
                            )
                        )
                        chatViewModel.addMessage(
                            ChatEntries(
                                message = typeMsg,
                                sender = userUiState.email,
                                receiver = selectedBatch.value + " " + selectedSemester.value,
                                isSent = true,
                                timeStamp = System.currentTimeMillis()
                            )
                        )
                    }
                    typeMsg=""
                }
            )

        }
    }
}