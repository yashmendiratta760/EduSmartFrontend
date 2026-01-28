package com.yash.edusmart.screens.teacher

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.outlined.Assignment
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
import com.yash.edusmart.viewmodel.MainAppUiState
import com.yash.edusmart.viewmodel.MainAppViewModel
import com.yash.edusmart.viewmodel.StudentUiState
import com.yash.edusmart.viewmodel.UserUiState

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TeacherMainLogic(navController: NavHostController,
                     mainAppUiState: MainAppUiState,
                     loginSignupViewModel: LoginSignupViewModel,
                     mainAppViewModel: MainAppViewModel,
                     chatViewModel: ChatViewModel,
                     userUiState: UserUiState,
                     chatUiState: ChatUiState,
                     studentUiState: StudentUiState,
                     loginUiState: LoginUiState){
    val context = LocalContext.current
    val selectedOption = remember { mutableStateOf("") }
    val bottomBarItems = listOf(
        Triple("Attendance", Icons.Outlined.Timelapse, Icons.Default.Timelapse),
        Triple("TimeTable", Icons.Outlined.CalendarMonth, Icons.Default.CalendarMonth),
        Triple("Assignments", Icons.Outlined.Assignment, Icons.Default.Assignment),
        Triple("Chat", Icons.Outlined.Group, Icons.Default.Group),
        Triple("Settings", Icons.Outlined.Settings, Icons.Default.Settings)
    )
    var selectedIndex by remember { mutableIntStateOf(0) }


    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val scrollBehaviorTimeTable = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollBehaviorChat = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollBehaviorSettings = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollBehaviorTasks = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val selectedBatch = remember { mutableStateOf("Select Branch") }
    val selectedSemester = remember { mutableStateOf("Select Semester") }
    val selectedChatType = remember { mutableStateOf("Select Chat Type") }
    var canNavigateBackChat by remember { mutableStateOf(false) }
    var chatBackPressed by remember { mutableStateOf(false) }

    LaunchedEffect(selectedIndex) {
        if(selectedIndex==2){
            mainAppViewModel.getAssignments()
        }
    }

    LaunchedEffect(selectedBatch.value, selectedSemester.value){

        if(selectedBatch.value!="Select Branch" && selectedSemester.value!="Select Semester") {
            mainAppViewModel.getTimeTableByBranchAndSemesterTeacher(selectedBatch.value, selectedSemester.value.toInt())
            mainAppViewModel.getTimeTableEntries(selectedBatch.value, selectedSemester.value.toInt())
        }
        Log.d("ENTRYYYY",mainAppUiState.timeTableEntries.toString())
    }



    Scaffold(
        topBar = {
            if(selectedIndex==0) {
                CustomTopBar(
                    navController = navController,
                    title = "EduSmart",
                    canNavigateBack = false,
                    userType = "Teacher",
                )
            }
            else if(selectedIndex == 1){
                TopAppBar(
                    scrollBehavior = scrollBehaviorTimeTable,
                    title = {
                        Text(text = "TimeTable",
                            fontSize = 35.sp)
                    }
                )
            }
            else if(selectedIndex==2){
                TopAppBar(
                    scrollBehavior = scrollBehaviorTasks,
                    title = {
                        Text(text = "Assignment",
                            fontSize = 35.sp)
                    }
                )
            }
            else if(selectedIndex==3){
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
            else if(selectedIndex == 4){
                TopAppBar(
                    scrollBehavior=scrollBehaviorSettings,
                    title = {
                        Text(text = "Settings",
                            fontSize = 35.sp)
                    }
                )
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
                mainAppUiState = mainAppUiState,
                mainAppViewModel = mainAppViewModel,
                navController = navController,
                scrollBehavior = scrollBehavior)
            2-> AssignmentScreen(innerPadding = innerPadding,
                chatViewModel=chatViewModel,
                chatUiState = chatUiState,
                navController = navController,
                mainAppUiState = mainAppUiState,
                userUiState = userUiState,
                mainAppViewModel = mainAppViewModel,
                isStudent = false)
            3-> ChatScreen(
                innerPadding = innerPadding,
                isStudent = false,
                mainAppViewModel = mainAppViewModel,
                mainAppUiState = mainAppUiState,
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
                studentUiState = studentUiState
            )
            4-> SettingsScreen(innerPadding = innerPadding,
                onEditProfileClick = {

                },
                loginSignupViewModel = loginSignupViewModel,
                navController = navController,
                userUiState = userUiState,
                loginUiState = loginUiState)

        }

    }
}