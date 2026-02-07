package com.yash.edusmart.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.yash.edusmart.navigation.Screens
import com.yash.edusmart.services.TokenManager
import com.yash.edusmart.viewmodel.ChatViewModel
import com.yash.edusmart.viewmodel.StudentViewModel
import com.yash.edusmart.viewmodel.TeacherViewModel
import kotlinx.coroutines.flow.first

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoadingScreen(navController: NavHostController,
                  teacherViewModel: TeacherViewModel,
                  chatViewModel: ChatViewModel,
                  studentViewModel: StudentViewModel)
{
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        val token = TokenManager.getToken(context).first()          // might be null
        val userType = TokenManager.getUserType(context).first()
        if (token.isNullOrBlank()) {
            navController.navigate(Screens.Login.name) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
            return@LaunchedEffect
        }

        chatViewModel.getAssignments()
        when (userType) {
            "STUDENT" -> {
                studentViewModel.getHolidaysServer()
                navController.navigate(Screens.Student.name) {
                    popUpTo(0) { inclusive = true }
                }
            }

            "TEACHER" -> {
                teacherViewModel.getAllBranch()
                teacherViewModel.getTimeTableTeacher()
                navController.navigate(Screens.Teacher.name) {
                    popUpTo(0) { inclusive = true }
                }
            }

            else -> {
                navController.navigate(Screens.Login.name) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }


    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator()
    }

}