package com.yash.edusmart.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.yash.edusmart.navigation.Screens
import com.yash.edusmart.services.SocketService
import com.yash.edusmart.services.TokenManager
import com.yash.edusmart.viewmodel.ChatViewModel
import com.yash.edusmart.viewmodel.MainAppViewModel
import com.yash.edusmart.viewmodel.StudentViewModel
import com.yash.edusmart.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoadingScreen(navController: NavHostController,
                  mainAppViewModel: MainAppViewModel,
                  chatViewModel: ChatViewModel,
                  studentViewModel: StudentViewModel,
                  userViewModel: UserViewModel)
{
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        val token = TokenManager.getToken(context).first()          // might be null
        val userType = TokenManager.getUserType(context).first()
        val email = TokenManager.getEmail(context).first()
        if (token.isNullOrBlank()) {
            navController.navigate(Screens.Login.name) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
            return@LaunchedEffect
        }

        if(token!=null) {
            chatViewModel.getAssignments()
            when (userType) {
                "STUDENT" -> {
                    studentViewModel.getHolidaysServer()
                    mainAppViewModel.getAssignmentStudent()
                    navController.navigate(Screens.Student.name) {
                        popUpTo(0) { inclusive = true }
                    }
                }

                "TEACHER" -> {
                    mainAppViewModel.getAllBranch()
                    if(email!=null)  mainAppViewModel.getTimeTableTeacher(email)
                    mainAppViewModel.getAssignments()
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
        }else{
            navController.navigate(Screens.Login.name) {
                popUpTo(0) { inclusive = true }
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