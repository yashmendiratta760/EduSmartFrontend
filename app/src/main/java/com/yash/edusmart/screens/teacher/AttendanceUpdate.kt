package com.yash.edusmart.screens.teacher

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yash.edusmart.data.AttendanceStatus
import com.yash.edusmart.navigation.Screens
import com.yash.edusmart.screens.component.CustomDropdownMenu
import com.yash.edusmart.screens.component.CustomTopBar
import com.yash.edusmart.screens.component.teacher.DatePickerMenu
import com.yash.edusmart.viewmodel.MainAppUiState
import com.yash.edusmart.viewmodel.MainAppViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AttendanceUpdate(mainAppUiState: MainAppUiState,
                     mainAppViewModel: MainAppViewModel,
                     navController: NavHostController){

    val branchSelected = remember { mutableStateOf("Select Branch") }
    val semSelected = remember { mutableStateOf("Select Semester") }
    val dateSelected = remember { mutableStateOf("") }
    val selectedTiming = remember { mutableStateOf("Select Time Slot") }
    val formatter = remember { DateTimeFormatter.ISO_LOCAL_DATE }
    val selectedDayName by remember(dateSelected.value) {
        derivedStateOf {
            val date = if (dateSelected.value.isNotEmpty())
                LocalDate.parse(dateSelected.value, formatter)
            else LocalDate.now()
            date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() } // "Monday"
        }
    }
    val timeTableEntriesTimings by remember(selectedDayName) {
        derivedStateOf {
            mainAppUiState.timeTableEntries
                .filter { it.day.equals(selectedDayName, ignoreCase = true) }
                .sortedBy { it.timing }
                .map { it.timing }
        }
    }
    val selectedStudentName = remember { mutableStateOf("Select Name") }

    val selectedStatus = remember { mutableStateOf("Select Attendance Status") }


    LaunchedEffect(branchSelected.value,semSelected.value){
        if(branchSelected.value != "Select Branch" && semSelected.value != "Select Semester"){
            mainAppViewModel.getStudentListTeacherAttendance(branch = branchSelected.value, semester = semSelected.value)
        }
    }
    val attendanceListBool = remember { mutableStateListOf<Pair<String,String>>() }
    LaunchedEffect(mainAppUiState.studentDataAttendance) {
        attendanceListBool.clear()
        attendanceListBool.addAll(
            mainAppUiState.studentDataAttendance.map { student ->
                Pair(student.name,student.email)
            }
        )
    }

    val subjectName by remember(selectedTiming.value, selectedDayName) {
        derivedStateOf {
            mainAppUiState.timeTableEntries.firstOrNull {
                it.timing == selectedTiming.value &&
                        it.day.equals(selectedDayName, ignoreCase = true)
            }?.subject ?: "Subject Name"
        }
    }

    val branches by remember(mainAppUiState.branch){
        derivedStateOf {
            mainAppUiState.branch.distinct()
        }
    }
    val semester = listOf("1","2","3","4","5","6","7","8")

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = {
                Text(text = "Attendance Update")
            },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Arrow")
                    }
                })
        }) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            item {
                CustomDropdownMenu(
                    options = branches,
                    selectedOption = branchSelected.value
                ) { selectedOpt ->
                    branchSelected.value = selectedOpt
                }
                CustomDropdownMenu(
                    options = semester,
                    selectedOption = semSelected.value
                ) { selectedOpt ->
                    semSelected.value = selectedOpt
                }

                DatePickerMenu(dateSelected)
                CustomDropdownMenu(
                    options = timeTableEntriesTimings,
                    selectedOption = selectedTiming.value
                ) { selectedOpt ->
                    selectedTiming.value = selectedOpt
                }
                CustomDropdownMenu(
                    options = attendanceListBool.map {
                        val name = it.first
                        val email = it.second
                        "$name ($email)"
                    },

                    selectedOption = selectedStudentName.value
                ) { name ->
                    selectedStudentName.value = name
                }
                CustomDropdownMenu(
                    options = listOf("PRESENT", "ABSENT"),
                    selectedOption = selectedStatus.value
                ) { status ->
                    selectedStatus.value = status
                }
                TextField(
                    value = subjectName,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(shape = RoundedCornerShape(25.dp))
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color(0xFF4949D3).copy(alpha = 0.5f),
                        unfocusedContainerColor = Color(0xFF4949D3).copy(alpha = 0.5f)
                    )
                )
            }
            item {

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onClick = {
                        val idx = selectedStudentName.value.indexOf("(")
                        val email = selectedStudentName.value.substring(
                            idx + 1,
                            selectedStudentName.value.length - 1
                        )
                        val attendanceList: List<AttendanceStatus> = listOf(
                            AttendanceStatus(
                                email = email,
                                status = selectedStatus.value
                            )
                        )

                        mainAppViewModel.uploadAttendance(
                            attendanceList = attendanceList,
                            time = selectedTiming.value,
                            branch = branchSelected.value,
                            semester = semSelected.value.toIntOrNull() ?:1,
                            date = dateSelected.value,
                            subjectName = subjectName
                        )
                    }) {
                    Text(text = "Submit")

                }

            }
        }
    }

}