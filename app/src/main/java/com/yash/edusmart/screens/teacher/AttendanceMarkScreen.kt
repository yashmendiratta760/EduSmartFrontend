package com.yash.edusmart.screens.teacher

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yash.edusmart.data.AttendanceStatus
import com.yash.edusmart.navigation.Screens
import com.yash.edusmart.screens.component.CustomDropdownMenu
import com.yash.edusmart.screens.component.teacher.DatePickerMenu
import com.yash.edusmart.viewmodel.MainAppUiState
import com.yash.edusmart.viewmodel.MainAppViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AttendanceMarkScreen(innerPadding: PaddingValues,
                         mainAppUiState: MainAppUiState,
                         mainAppViewModel: MainAppViewModel,
                         navController: NavHostController,
                         scrollBehavior: TopAppBarScrollBehavior)
{

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        mainAppViewModel.getAllBranch()
    }



    val branchSelected = remember { mutableStateOf("Select Branch") }
    val semSelected = remember { mutableStateOf("Select Semester") }
    val dateSelected = remember { mutableStateOf("") }
    val selectedTiming = remember { mutableStateOf("Select Time Slot") }
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    val selectedDayName by remember(dateSelected.value) {
        derivedStateOf {
            val date = if (dateSelected.value.isNotEmpty())
                LocalDate.parse(dateSelected.value, formatter)
            else LocalDate.now()
            date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() } // "Monday"
        }
    }

    LaunchedEffect(branchSelected.value,semSelected.value){
        if(branchSelected.value != "Select Branch" && semSelected.value != "Select Semester"){
            mainAppViewModel.getStudentListTeacherAttendance(branch = branchSelected.value, semester = semSelected.value)
            mainAppViewModel.getTimeTableByBranchAndSemesterTeacher(branch = branchSelected.value, semester = semSelected.value.toInt())
            mainAppViewModel.getTimeTableEntries(branch = branchSelected.value, semester = semSelected.value.toInt())
            Log.d("HITT",mainAppUiState.timeTableEntries.toString())
        }
    }

    val timeTableEntriesTimings by remember(selectedDayName,mainAppUiState.timeTableEntries) {
        derivedStateOf {
            mainAppUiState.timeTableEntries
                .filter { it.day.equals(selectedDayName, ignoreCase = true) }
                .sortedBy { it.timing }
                .map { it.timing }
        }
    }



    val attendanceListBool = remember { mutableStateListOf<StudentAttendance>() }

    LaunchedEffect(mainAppUiState.studentDataAttendance) {
        attendanceListBool.clear()
        attendanceListBool.addAll(
            mainAppUiState.studentDataAttendance.map { student ->
                StudentAttendance(student.email, null,student.name)
            }
        )
    }


    val subjectName by remember(selectedTiming.value,
        selectedDayName,
        mainAppUiState.timeTableEntries) {
        derivedStateOf {
            mainAppUiState.timeTableEntries.firstOrNull {
                it.timing == selectedTiming.value &&
                        it.day.equals(selectedDayName, ignoreCase = true)
            }?.subject ?: "Subject Name"
        }
    }
    Log.d("TEACHER",selectedDayName.uppercase())
//    Log.d("TEACHER 2",timeTableEntriesTimings.firstOrNull() ?:"NULL@@")
    val branches = mainAppUiState.branch.distinct()
    val semester = listOf("1","2","3","4","5","6","7","8")
    LazyColumn(modifier = Modifier.padding(innerPadding)
        .fillMaxSize()
        ) {
        item {
            CustomDropdownMenu(
                options = branches,
                selectedOption = branchSelected
            ) { selectedOpt ->
                branchSelected.value = selectedOpt
            }
            CustomDropdownMenu(
                options = semester,
                selectedOption = semSelected
            ) { selectedOpt ->
                semSelected.value = selectedOpt
            }
            DatePickerMenu(dateSelected)
            CustomDropdownMenu(options = timeTableEntriesTimings,
                selectedOption = selectedTiming){selectedOpt->
                selectedTiming.value = selectedOpt
            }
            TextField(value = subjectName,
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
        itemsIndexed(attendanceListBool, key = { _, s -> s.email }) { index, student ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = student.name,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )

                Row(verticalAlignment = Alignment.CenterVertically) {

                    // PRESENT
                    RadioButton(
                        selected = student.isPresent == true,
                        onClick = {
                            attendanceListBool[index] =
                                student.copy(
                                    isPresent = if (student.isPresent == true) null else true
                                )
                        }
                    )
                    Text("P")

                    // ABSENT
                    RadioButton(
                        selected = student.isPresent == false,
                        onClick = {
                            attendanceListBool[index] =
                                student.copy(
                                    isPresent = if (student.isPresent == false) null else false
                                )
                        }
                    )
                    Text("A")
                }
            }
        }
        item{
            Column {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onClick = {

                        val attendanceList: List<AttendanceStatus> =
                            attendanceListBool
                                .filter { it.isPresent != null } // ✅ skip blank
                                .map {
                                    AttendanceStatus(
                                        email = it.email,
                                        status = if (it.isPresent == true) "PRESENT" else "ABSENT"
                                    )
                                }
                        if (attendanceList.isEmpty()) {
                            Toast.makeText(context, "Mark at least one student", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        mainAppViewModel.uploadAttendance(
                            attendanceList = attendanceList,
                            time = selectedTiming.value,
                            branch = branchSelected.value,
                            semester = semSelected.value.toIntOrNull() ?:0,
                            date = dateSelected.value,
                            subjectName = subjectName
                        )
                    }) {
                    Text(text = "Submit")

                }
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    Text(text = "or ",
                        fontSize = 18.sp)
                    Text(text = "Update the previous attendance",
                        color = Color.Blue,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = {
                            navController.navigate(Screens.Attendance_Update.name)
                        }))
                }
                Spacer(modifier = Modifier.padding(20.dp))
            }
        }
    }
}

data class StudentAttendance(
    val email: String,
    var isPresent: Boolean?,
    val name: String
)
