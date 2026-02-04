package com.yash.edusmart.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yash.edusmart.login_signup.screens.Login
import com.yash.edusmart.screens.LoadingScreen
import com.yash.edusmart.screens.MainLogic
import com.yash.edusmart.screens.teacher.AssignmentUploadDataScreen
import com.yash.edusmart.screens.teacher.AttendanceUpdate
import com.yash.edusmart.screens.teacher.TeacherMainLogic
import com.yash.edusmart.viewmodel.ChatViewModel
import com.yash.edusmart.viewmodel.LoginSignupViewModel
import com.yash.edusmart.viewmodel.StudentViewModel
import com.yash.edusmart.viewmodel.TeacherViewModel
import com.yash.edusmart.viewmodel.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(navController: NavHostController = rememberNavController(),
               loginSignupViewModel: LoginSignupViewModel,
               teacherViewModel: TeacherViewModel,
               studentViewModel: StudentViewModel,
               chatViewModel: ChatViewModel,
               userViewModel: UserViewModel,
               isDarkTheme:Boolean= isSystemInDarkTheme())
{
    val teacherUiState by teacherViewModel.uiState.collectAsState()
    val studentUiState by studentViewModel.studentUiState.collectAsState()
    val loginUiState by loginSignupViewModel.uiState.collectAsState()
    val userUiState by userViewModel.uiState.collectAsState()
    val chatUiState by chatViewModel.uiState.collectAsState()
    NavHost(navController=navController,
        startDestination = Screens.Loading.name){


        composable(route = Screens.Login.name){
            Login(loginSignupViewModel,navController,
                isDarkTheme,
                loginUiState=loginUiState)
        }
        composable(route = Screens.Loading.name){
            LoadingScreen(
                navController = navController,
                chatViewModel = chatViewModel,
                studentViewModel=studentViewModel,
                teacherViewModel = teacherViewModel)
        }
        composable(route = Screens.Student.name){
            MainLogic(navController = navController,
                loginSignupViewModel=loginSignupViewModel,
                teacherViewModel=teacherViewModel,
                teacherUiState = teacherUiState,
                studentUiState = studentUiState,
                studentViewModel = studentViewModel,
                chatViewModel = chatViewModel,
                userUiState = userUiState,
                loginUiState = loginUiState,
                chatUiState = chatUiState)
        }

        composable(route = Screens.Teacher.name){
            TeacherMainLogic(navController = navController,
                loginSignupViewModel = loginSignupViewModel,
                teacherViewModel=teacherViewModel,
                teacherUiState = teacherUiState,
                studentViewModel = studentViewModel,
                chatViewModel = chatViewModel,
                userUiState=userUiState,
                loginUiState = loginUiState,
                studentUiState = studentUiState,
                chatUiState = chatUiState)
        }

        composable(route = Screens.Attendance_Update.name){
            AttendanceUpdate(
                teacherViewModel=teacherViewModel,
                teacherUiState = teacherUiState,
                navController = navController)
        }
        composable(route = Screens.Assignment_DATA.name) {
            AssignmentUploadDataScreen(
                chatViewModel = chatViewModel,
                userUiState = userUiState,
                teacherViewModel=teacherViewModel,
                teacherUiState = teacherUiState,
                navController=navController
            )
        }

    }
}