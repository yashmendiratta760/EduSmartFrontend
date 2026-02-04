package com.yash.edusmart.screens.teacher

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.app.DatePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yash.edusmart.data.AssignmentDTO
import com.yash.edusmart.screens.component.CustomDropdownMenu
import com.yash.edusmart.viewmodel.ChatViewModel
import com.yash.edusmart.viewmodel.MainAppUiState
import com.yash.edusmart.viewmodel.MainAppViewModel
import com.yash.edusmart.viewmodel.UserUiState
import java.time.LocalDate
import java.time.ZoneId



@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AssignmentUploadDataScreen(
    chatViewModel: ChatViewModel,
    userUiState: UserUiState,
    mainAppUiState: MainAppUiState,
    navController: NavHostController,
    mainAppViewModel: MainAppViewModel
) {

    val context = LocalContext.current
    LaunchedEffect(chatViewModel) {
        chatViewModel.toastEvent.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(mainAppViewModel) {
        mainAppViewModel.toastEvent.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val branches by remember(mainAppUiState.branch){
        derivedStateOf {
            mainAppUiState.branch.distinct()
        }
    }
    val batch = remember { mutableStateOf("Select batch") }
    val sem = remember { mutableStateOf("Select Semester") }


    // DatePickerDialog
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            },
            LocalDate.now().year,
            LocalDate.now().monthValue - 1,
            LocalDate.now().dayOfMonth
        )
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(text = "Upload Assignment",
                        fontSize = 35.sp)
                },
                navigationIcon = {

                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Arrow")
                    }

                }
            )
        }
    ){ innerPadding->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Text(
                text = "Create Assignment",
                style = MaterialTheme.typography.headlineSmall
            )


            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Task Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            CustomDropdownMenu(
                options = branches,
                selectedOption = batch.value
            ) { b ->
                batch.value = b
            }

            CustomDropdownMenu(
                options = listOf("1","2","3","4","5","6","7","8"),
                selectedOption = sem.value
            ) { s ->
                sem.value = s
            }



            OutlinedTextField(
                value = selectedDate?.toString() ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                        datePickerDialog.show()
                    }) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "Calendar",
                        )
                    }
                },
                label = { Text("Deadline Date") },
                modifier = Modifier
                    .fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    selectedDate?.let { date ->
                        val deadlineMillis = date
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()

                        chatViewModel.sendAssignment(
                            message = AssignmentDTO(
                                sender = userUiState.email,
                                receiver = batch.value + " " +sem.value,
                                task = description,
                                deadline = deadlineMillis
                            ),
                            groupId = batch.value + " " +sem.value
                        )
                    }

                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = description.isNotBlank() && selectedDate != null
            ) {
                Text("Submit Assignment")
            }
        }
    }
}
