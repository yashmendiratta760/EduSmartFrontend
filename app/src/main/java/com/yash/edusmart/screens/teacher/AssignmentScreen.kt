package com.yash.edusmart.screens.teacher

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yash.edusmart.data.AssignmentGetDTO
import com.yash.edusmart.db.Assignments
import com.yash.edusmart.navigation.Screens
import com.yash.edusmart.screens.component.CustomDropdownMenu
import com.yash.edusmart.screens.component.student.TaskAlert
import com.yash.edusmart.viewmodel.ChatUiState
import com.yash.edusmart.viewmodel.ChatViewModel
import com.yash.edusmart.viewmodel.StudentUiState
import com.yash.edusmart.viewmodel.StudentViewModel
import com.yash.edusmart.viewmodel.TeacherUiState
import com.yash.edusmart.viewmodel.TeacherViewModel
import com.yash.edusmart.viewmodel.UserUiState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AssignmentScreen(innerPadding: PaddingValues,
                     chatUiState: ChatUiState,
                     chatViewModel: ChatViewModel,
                     isStudent: Boolean,
                     studentUiState: StudentUiState,
                     studentViewModel: StudentViewModel,
                     teacherUiState: TeacherUiState,
                     teacherViewModel: TeacherViewModel,
                     userUiState: UserUiState,
                     navController: NavHostController){


    val branches by remember(teacherUiState.branch) {
        derivedStateOf {
            teacherUiState.branch.distinct()
        }
    }
    val semester = listOf("1","2","3","4","5","6","7","8")
    val branchSelected = remember { mutableStateOf("Select Branch") }
    val semSelected = remember { mutableStateOf("Select Semester") }

    LaunchedEffect(branchSelected.value,semSelected.value){
        if(branchSelected.value!="Select Branch" && semSelected.value!="Select Semester"){
            teacherViewModel.getAssignmentsTeacher(branchSelected.value, semSelected.value)
        }
    }

    var assigns by remember {
        mutableStateOf<List<Assignments>>(emptyList())
    }
    var assignsT by remember {
        mutableStateOf<List<AssignmentGetDTO>>(emptyList())
    }
    if(isStudent) {
        LaunchedEffect(chatUiState.assignments) {
            assigns = chatUiState.assignments
        }
    }else{
        LaunchedEffect(teacherUiState.assignments) {
            assignsT = teacherUiState.assignments
        }
    }

    val today = LocalDate.now()

    val studentAssignments by remember(assigns) {
        derivedStateOf { assigns }   // show all
    }


    val pullState = rememberPullToRefreshState()
    PullToRefreshBox(
        state = pullState,
        onRefresh = {
            if(isStudent) studentViewModel.getAssignmentStudent(userUiState.branch,userUiState.semester)
            else{
                if(branchSelected.value!="Select Branch" && semSelected.value!="Select Semester")
                    teacherViewModel.getAssignmentsTeacher(branchSelected.value,semSelected.value)
            }
        },
        modifier = Modifier.padding(innerPadding),
        isRefreshing = if(isStudent) studentUiState.isLoading else teacherUiState.isLoading
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            LazyColumn {
                if (isStudent) {
                    items(studentAssignments) { a ->
                        var checked by remember { mutableStateOf(a.isCompleted) }
                        val match = Regex("""^\(([^)]*)\)(.*)$""").find(a.task)
                        val task = match?.groupValues[1]?:"Hello".trim().replaceFirstChar { it.uppercase() }
                        val desc = match?.groupValues[2]?:"Hello".trim()
                        val deadlineDate = a.deadline.toLocalDate()
                        val deadlineText = if (deadlineDate.isBefore(today)) "Expired"
                        else chatViewModel.formatDate(a.deadline)
                        TaskAlert(
                            heading = task,
                            task = desc,
                            deadline = deadlineText,
                            isStudent = true,
                            checked = checked,
                            canSubmit = deadlineText!="Expired",
                            onSubmit = {
                                studentViewModel.markAssignment(a.id, userUiState.enroll)
                            },
                            onCheckedChange = {
                                checked = !checked
                                studentViewModel.updateIsCompleted(a.id, checked)
                            }
                        )
                    }
                } else {
                    item {
                        CustomDropdownMenu(
                            options = branches,
                            selectedOption = branchSelected.value
                        ) { opt ->
                            branchSelected.value = opt
                        }
                        CustomDropdownMenu(
                            options = semester,
                            selectedOption = semSelected.value
                        ) { opt ->
                            semSelected.value = opt
                        }
                    }
                    items(assignsT) { a ->
                        val match = Regex("""^\(([^)]*)\)(.*)$""").find(a.assignment)
                        val task = match?.groupValues[1]?:"Hello".trim().replaceFirstChar { it.uppercase() }
                        val desc = match?.groupValues[2]?:"Hello".trim()
                        TaskAlert(
                            heading = task,
                            task = desc,
                            deadline = chatViewModel.formatDate(a.deadline),
                            isStudent = false,
                            isTeacher = true,
                            completedByNames = a.enroll,
                            onDeleteClick = {
                                teacherViewModel.deleteById(
                                    id = a.id,
                                    branchSelected.value,
                                    semSelected.value
                                )
                            }
                        )
                    }
                }
            }
            if (!isStudent) {

                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF68DE50),
                            shape = CircleShape
                        )

                        .align(Alignment.BottomEnd)
                        .size(60.dp)
                        .padding(10.dp)
                        .clickable(
                            onClick = {
                                navController.navigate(Screens.Assignment_DATA.name)
                            }
                        ),
                    contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "POST",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}


