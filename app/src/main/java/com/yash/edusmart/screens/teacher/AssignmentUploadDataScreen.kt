package com.yash.edusmart.screens.teacher

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yash.edusmart.api.PresignUploadRequest
import com.yash.edusmart.data.AssignmentDTO
import com.yash.edusmart.helper.queryDisplayName
import com.yash.edusmart.helper.uploadToSignedUrl
import com.yash.edusmart.screens.component.CustomDropdownMenu
import com.yash.edusmart.viewmodel.ChatViewModel
import com.yash.edusmart.viewmodel.TeacherUiState
import com.yash.edusmart.viewmodel.TeacherViewModel
import com.yash.edusmart.viewmodel.UserUiState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AssignmentUploadDataScreen(
    chatViewModel: ChatViewModel,
    userUiState: UserUiState,
    teacherUiState: TeacherUiState,
    navController: NavHostController,
    teacherViewModel: TeacherViewModel
) {




    val context = LocalContext.current
    var pickedUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var selectedFileName by remember { mutableStateOf<String>("Select File") }
    val pickFile = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            pickedUri = uri
            selectedFileName = queryDisplayName(context, uri)
        }
    }

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
    var head by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    val branches by remember(teacherUiState.branch){
        derivedStateOf {
            teacherUiState.branch.distinct()
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {

                Text(
                    text = "Create Assignment",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    value = head,
                    onValueChange = { head = it },
                    label = { Text("Heading") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
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
                    options = listOf("1", "2", "3", "4", "5", "6", "7", "8"),
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

                OutlinedTextField(
                    value = selectedFileName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            pickFile.launch(arrayOf(
                                "application/pdf",
                                "image/*",
                                "application/msword",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                            ))
                        }) {
                            Icon(
                                Icons.Default.Attachment,
                                contentDescription = "Attachment",
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
                        val date = selectedDate ?: return@Button
                        val deadlineMillis = date
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()

                        val task = "($head)$description"
                        val group = batch.value + " " + sem.value

                        scope.launch {
                            try {
                                val uri = pickedUri
                                var attachmentPath: String? = null
                                isUploading = true

                                // 1) If file picked -> presign + upload to Supabase
                                val presign = teacherViewModel.presignUploadSuspend(
                                    PresignUploadRequest(fileName = selectedFileName)
                                )

                                // 2) upload bytes to signed URL (UI has context/uri)
                                val supabaseBaseUrl = "https://ukaftdwlcwjbustjuczm.supabase.co/storage/v1"

                                uploadToSignedUrl(
                                    context = context,
                                    uri = uri!!,
                                    fullUploadUrl = supabaseBaseUrl + presign.uploadUrl
                                )

                                // 3) permanent path
                                attachmentPath = presign.path

                                // 2) Send assignment through WS (same as now)
                                chatViewModel.sendAssignment(
                                    message = AssignmentDTO(
                                        sender = userUiState.email,
                                        receiver = group,
                                        task = task,
                                        deadline = deadlineMillis,
                                        path = attachmentPath
                                    ),
                                    groupId = group
                                )

                                navController.popBackStack()
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message ?: "Upload failed", Toast.LENGTH_SHORT).show()
                            } finally {
                                isUploading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = description.isNotBlank() && selectedDate != null && !isUploading
                ) {
                    Text(if (isUploading) "Uploading..." else "Submit Assignment")
                }
            }
        }
    }
}
