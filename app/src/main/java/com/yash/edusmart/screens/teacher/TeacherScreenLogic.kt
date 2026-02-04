package com.yash.edusmart.screens.teacher

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timelapse
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yash.edusmart.screens.SettingsScreen
import com.yash.edusmart.screens.component.CustomBottomNavigationBar
import com.yash.edusmart.screens.component.CustomTopBar
import com.yash.edusmart.screens.student.ChatScreen
import com.yash.edusmart.viewmodel.ChatUiState
import com.yash.edusmart.viewmodel.ChatViewModel
import com.yash.edusmart.viewmodel.LoginSignupViewModel
import com.yash.edusmart.viewmodel.LoginUiState
import com.yash.edusmart.viewmodel.StudentUiState
import com.yash.edusmart.viewmodel.StudentViewModel
import com.yash.edusmart.viewmodel.TeacherUiState
import com.yash.edusmart.viewmodel.TeacherViewModel
import com.yash.edusmart.viewmodel.UserUiState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TeacherMainLogic(navController: NavHostController,
                     teacherUiState: TeacherUiState,
                     loginSignupViewModel: LoginSignupViewModel,
                     teacherViewModel: TeacherViewModel,
                     chatViewModel: ChatViewModel,
                     userUiState: UserUiState,
                     chatUiState: ChatUiState,
                     studentUiState: StudentUiState,
                     studentViewModel: StudentViewModel,
                     loginUiState: LoginUiState){
    val context = LocalContext.current
    val bottomBarItems = listOf(
        Triple("Attendance", Icons.Outlined.Timelapse, Icons.Default.Timelapse),
        Triple("TimeTable", Icons.Outlined.CalendarMonth, Icons.Default.CalendarMonth),
        Triple("Assignments", Icons.AutoMirrored.Outlined.Assignment,
            Icons.AutoMirrored.Filled.Assignment
        ),
        Triple("Chat", Icons.Outlined.Group, Icons.Default.Group),
        Triple("Settings", Icons.Outlined.Settings, Icons.Default.Settings)
    )
    var selectedIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(chatViewModel) {
        chatViewModel.toastEvent.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(teacherViewModel) {
        teacherViewModel.toastEvent.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }



    val scrollBehaviorTimeTable = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollBehaviorChat = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollBehaviorSettings = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollBehaviorTasks = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val selectedBatch = remember { mutableStateOf("Select Branch") }
    val selectedSemester = remember { mutableStateOf("Select Semester") }
    val selectedChatType = remember { mutableStateOf("Select Chat Type") }
    var canNavigateBackChat by remember { mutableStateOf(false) }
    var chatBackPressed by remember { mutableStateOf(false) }


    val selectedDay = remember { mutableStateOf(LocalDate.now().dayOfWeek.toString().lowercase().replaceFirstChar { it.uppercase() }) }

    LaunchedEffect(selectedBatch.value, selectedSemester.value){

        if(selectedBatch.value!="Select Branch" && selectedSemester.value!="Select Semester") {
            teacherViewModel.getTimeTableByBranchAndSemesterTeacher(selectedBatch.value, selectedSemester.value.toInt())
            teacherViewModel.getTimeTableEntries(selectedBatch.value, selectedSemester.value.toInt())
        }
    }



    Scaffold(
        topBar = {
            when (selectedIndex) {
                0 -> {
                    CustomTopBar(
                        navController = navController,
                        title = "EduSmart",
                        canNavigateBack = false,
                        userType = "Teacher",
                    )
                }
                1 -> {
                    TopAppBar(
                        scrollBehavior = scrollBehaviorTimeTable,
                        title = {
                            Text(text = "TimeTable",
                                fontSize = 35.sp)
                        }
                    )
                }
                2 -> {
                    TopAppBar(
                        scrollBehavior = scrollBehaviorTasks,
                        title = {
                            Text(text = "Assignment",
                                fontSize = 35.sp)
                        }
                    )
                }
                3 -> {
                    TopAppBar(
                        scrollBehavior = scrollBehaviorChat,
                        title = {
                            Text(text = "Chat Box",
                                fontSize = 35.sp)
                        },
                        navigationIcon = {
                            if(canNavigateBackChat){
                                IconButton(onClick = {
                                    chatBackPressed=true
                                }) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back Arrow")
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
        },
        bottomBar = {
            CustomBottomNavigationBar(
                selectedIndex = selectedIndex,
                onItemSelected = {index->
                    selectedIndex=index
                },
                items = bottomBarItems
            )
        }
    ) { innerPadding->
        when(selectedIndex){

            0-> AttendanceMarkScreen(innerPadding = innerPadding,
                teacherUiState = teacherUiState,
                teacherViewModel = teacherViewModel,
                navController = navController)

            1->TimeTableTeacher(
                innerPadding = innerPadding,
                selectedDay = selectedDay,
                teacherUiState = teacherUiState
            )
            2-> AssignmentScreen(innerPadding = innerPadding,
                chatViewModel=chatViewModel,
                chatUiState = chatUiState,
                navController = navController,
                teacherUiState = teacherUiState,
                userUiState = userUiState,
                teacherViewModel = teacherViewModel,
                studentUiState = studentUiState,
                studentViewModel = studentViewModel,
                isStudent = false)
            3-> ChatScreen(
                innerPadding = innerPadding,
                isStudent = false,
                teacherViewModel = teacherViewModel,
                teacherUiState = teacherUiState,
                chatViewModel = chatViewModel,
                selectedChatType = selectedChatType,
                selectedBatch = selectedBatch,
                selectedSemester = selectedSemester,
                canNavigateBack = {back->
                    canNavigateBackChat=back
                    chatBackPressed=false
                },
                onBackClick = chatBackPressed,
                userUiState = userUiState,
                studentUiState = studentUiState,
                studentViewModel = studentViewModel
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