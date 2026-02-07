package com.yash.edusmart.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlaylistAddCheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlaylistAddCheckCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yash.edusmart.screens.component.CustomBottomNavigationBar
import com.yash.edusmart.screens.component.CustomTopBar
import com.yash.edusmart.screens.student.AttendanceView
import com.yash.edusmart.screens.student.ChatScreen
import com.yash.edusmart.screens.student.HomeScreen
import com.yash.edusmart.screens.teacher.AssignmentScreen
import com.yash.edusmart.viewmodel.ChatUiState
import com.yash.edusmart.viewmodel.ChatViewModel
import com.yash.edusmart.viewmodel.LoginSignupViewModel
import com.yash.edusmart.viewmodel.LoginUiState
import com.yash.edusmart.viewmodel.StudentUiState
import com.yash.edusmart.viewmodel.StudentViewModel
import com.yash.edusmart.viewmodel.TeacherUiState
import com.yash.edusmart.viewmodel.TeacherViewModel
import com.yash.edusmart.viewmodel.UserUiState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainLogic(navController: NavHostController,
               loginSignupViewModel: LoginSignupViewModel,
              studentUiState: StudentUiState,
              studentViewModel: StudentViewModel,
              chatViewModel: ChatViewModel,
              userUiState: UserUiState,
              chatUiState: ChatUiState,
              teacherViewModel: TeacherViewModel,
              teacherUiState: TeacherUiState)
{
    val context  = LocalContext.current
    LaunchedEffect(userUiState.branch,userUiState.semester){
        if(userUiState.branch!="" && userUiState.semester.toInt()!=0) {
            studentViewModel.getTimeTableByBranchAndSemester(
                userUiState.branch,
                userUiState.semester.toInt()
            )
        }

    }

    LaunchedEffect(Unit) {
        launch {
            studentViewModel.toastEvent.collect {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
        launch {
            chatViewModel.toastEvent.collect {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(userUiState.email) {
        if (userUiState.email!="null@null.com") {
            studentViewModel.getAttendance(userUiState.email)
        }
    }
    LaunchedEffect(userUiState.branch,userUiState.semester) {
        if(userUiState.branch!="" && userUiState.semester.toInt()!=0)
            studentViewModel.getAssignmentStudent(userUiState.branch,userUiState.semester)
    }
    LaunchedEffect(studentUiState.callComplete,studentUiState.timeTableEntries ){
        studentViewModel.getTimeTableEntries(userUiState.branch, userUiState.semester.toInt())
        studentViewModel.setFalseCallComplete()

    }
    val selectedDay = rememberSaveable { mutableStateOf(LocalDate.now().dayOfWeek.toString().lowercase().replaceFirstChar { it.uppercase()  }) }

//    LaunchedEffect(selectedDay.value){
//        studentViewModel.setDay(selectedDay.value)
//    }

    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

//    LaunchedEffect(selectedIndex){
//        studentViewModel.setScreen(selectedIndex)
//    }

    val items = listOf(
        Triple("Home", Icons.Outlined.Home, Icons.Default.Home),
        Triple("Attendance", Icons.Outlined.CalendarMonth, Icons.Default.CalendarMonth),
        Triple("Tasks", Icons.Outlined.PlaylistAddCheckCircle, Icons.Default.PlaylistAddCheckCircle),
        Triple("Group", Icons.Outlined.Group, Icons.Default.Group),
        Triple("Settings", Icons.Outlined.Settings, Icons.Default.Settings)
    )
    val selectedChatType = remember { mutableStateOf("Select Chat Type") }
    val scrollBehaviorAttendance = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollBehaviorSettings = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollBehaviorTasks = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var canNavigateBackChat by remember { mutableStateOf(false) }
    var chatBackPressed by remember { mutableStateOf(false) }

    Scaffold(
        modifier = when(selectedIndex) {
            1-> Modifier.nestedScroll(scrollBehaviorAttendance.nestedScrollConnection)
            4-> Modifier.nestedScroll(scrollBehaviorSettings.nestedScrollConnection)
            else -> Modifier
        },
        bottomBar = {
            CustomBottomNavigationBar(
                selectedIndex=selectedIndex,
                items = items,
                onItemSelected = {index->
                    selectedIndex=index
                })
        },
        topBar = {
            when (selectedIndex) {
                0 -> {
                    CustomTopBar(
                        navController = navController,
                        title = "EduSmart",
                        canNavigateBack = false,
                        userType = "Student",
                    )
                }
                1 -> {
                    TopAppBar(
                        scrollBehavior = scrollBehaviorAttendance,
                        title = {
                            Text(text = "Attendance Tracker",
                                fontSize = 35.sp)
                        }
                    )
                }
                2 -> {
                    TopAppBar(
                        scrollBehavior = scrollBehaviorTasks,
                        title = {
                            Text(text = "Tasks",
                                fontSize = 35.sp)
                        }
                    )
                }
                3 -> {
                    TopAppBar(
                        scrollBehavior = scrollBehaviorTasks,
                        title = {
                            Text(text = "Chat Box",
                                fontSize = 35.sp)
                        },
                        navigationIcon = {
                            if (canNavigateBackChat) {
                                IconButton(onClick = {
                                    chatBackPressed = true
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back Arrow"
                                    )
                                }
                            }
                        }
                    )
                }
                4 -> {
                    TopAppBar(
                        scrollBehavior=scrollBehaviorSettings,
                        title = {
                            Text(text = "Settings",
                                fontSize = 35.sp)
                        }
                    )
                }
            }
        }
    ) {innerPadding->
        when(selectedIndex){
            0->HomeScreen(innerPadding = innerPadding,
                selectedDay = selectedDay,
                studentUiState = studentUiState,
                chatUiState = chatUiState,
                studentViewModel=studentViewModel,
                userUiState=userUiState
                ){index->
                selectedIndex = index
            }
            1-> AttendanceView(innerPadding = innerPadding,
                studentUiState = studentUiState,
                studentViewModel = studentViewModel,
                userUiState = userUiState)

            2-> AssignmentScreen(innerPadding=innerPadding,
                chatUiState = chatUiState,
                chatViewModel = chatViewModel,
                isStudent = true,
                studentUiState = studentUiState,
                studentViewModel = studentViewModel,
                userUiState=userUiState,
                teacherUiState = teacherUiState,
                teacherViewModel = teacherViewModel,
                navController=navController)
            3-> ChatScreen(
                innerPadding = innerPadding,
                isStudent = true,
                studentUiState =  studentUiState,
                studentViewModel = studentViewModel,
                chatViewModel = chatViewModel,
                selectedChatType = selectedChatType,
                canNavigateBack = {back->
                    canNavigateBackChat=back
                    chatBackPressed=false
                },
                onBackClick = chatBackPressed,
                userUiState = userUiState,
                teacherViewModel = teacherViewModel,
                teacherUiState = teacherUiState
            )

            4-> SettingsScreen(innerPadding = innerPadding,
                onEditProfileClick = {

                },
                loginSignupViewModel = loginSignupViewModel,
                navController = navController,
                userUiState = userUiState)
        }


    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun getBoxColor(text:String, time: String): Color {

    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val currentTime = LocalTime.now()
    val subTimeIdx = time.indexOf("-")
    if (subTimeIdx == -1) return Color.Cyan
    val timeSub = LocalTime.parse(time.substring(subTimeIdx+1).trim(),formatter)

    val colors = if(timeSub.isBefore(currentTime)) Color.Gray
    else if(text.contains("/")) Color.Magenta
    else if(text == "Break") Color.Blue
    else Color.Cyan
    return colors
}


