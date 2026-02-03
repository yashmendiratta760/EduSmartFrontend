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
import androidx.compose.material3.Icon
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
import com.yash.edusmart.screens.component.student.TaskAlert
import com.yash.edusmart.viewmodel.ChatUiState
import com.yash.edusmart.viewmodel.ChatViewModel
import com.yash.edusmart.viewmodel.MainAppUiState
import com.yash.edusmart.viewmodel.MainAppViewModel
import com.yash.edusmart.viewmodel.UserUiState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.collections.emptyList

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AssignmentScreen(innerPadding: PaddingValues,
                     chatUiState: ChatUiState,
                     chatViewModel: ChatViewModel,
                     isStudent: Boolean,
                     mainAppUiState: MainAppUiState,
                     mainAppViewModel: MainAppViewModel,
                     userUiState: UserUiState,
                     navController: NavHostController){

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
        LaunchedEffect(mainAppUiState.assignments) {
            assignsT = mainAppUiState.assignments
        }
    }

    val today = LocalDate.now()

    val filteredStudentAssignments by remember(assigns) {
        derivedStateOf {
            assigns.filter { a ->
                val deadlineDate = a.deadline.toLocalDate()
                !deadlineDate.isBefore(today)   // ✅ today OR future
            }
        }
    }

    Box(modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize()) {

        LazyColumn {
            if(isStudent) {
                items(filteredStudentAssignments) { a ->
                    var checked by remember { mutableStateOf(a.isCompleted) }
                    TaskAlert(
                        heading = "Assignment",
                        task = a.task,
                        deadline = chatViewModel.formatDate(a.deadline),
                        isStudent = true,
                        checked = checked,
                        onSubmit = {
                            mainAppViewModel.markAssignment(a.id,userUiState.enroll)
                        },
                        onCheckedChange = {
                            checked=!checked
                            mainAppViewModel.updateIsCompleted(a.id,checked)
                        }
                    )
                }
            }else{
                items(assignsT) { a ->
                    TaskAlert(
                        heading = "Assignment",
                        task = a.assignment,
                        deadline = chatViewModel.formatDate(a.deadline),
                        isStudent = false,
                        isTeacher = true,
                        completedByNames = a.enroll,
                        onDeleteClick = {
                            mainAppViewModel.deleteById(id = a.id)
                        }
                    )
                }
            }
        }
        if(!isStudent) {

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

@RequiresApi(Build.VERSION_CODES.O)
fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}


