package com.yash.edusmart.screens.student

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yash.edusmart.api.AttendanceDTO
import com.yash.edusmart.screens.component.CalendarView
import com.yash.edusmart.screens.component.CustomDropdownMenu
import com.yash.edusmart.viewmodel.MainAppUiState
import com.yash.edusmart.viewmodel.MainAppViewModel
import com.yash.edusmart.viewmodel.StudentUiState
import com.yash.edusmart.viewmodel.StudentViewModel
import com.yash.edusmart.viewmodel.UserUiState
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AttendanceView(navController: NavHostController,
                   innerPadding: PaddingValues,
                   studentUiState: StudentUiState,
                   mainAppViewModel: MainAppViewModel,
                   studentViewModel: StudentViewModel,
                   userUiState: UserUiState,
                   mainAppUiState: MainAppUiState){
    val context = LocalContext.current


    val options by remember(mainAppUiState.subjectList) {
        derivedStateOf {
            mainAppUiState.subjectList
                .filter { it != "BREAK" }   // remove BREAK
                .distinct()                 // keep only unique values
        }
    }


    val selectedOption = remember { mutableStateOf(studentUiState.selectedSubject) }

    LaunchedEffect(selectedOption.value){
        studentViewModel.setSubject(selectedOption.value)
    }

    val dates = mainAppUiState.attendance.map { Triple(it.date, it.status, it.subject) }

    val filteredAttendance:List<AttendanceDTO> = if (selectedOption.value == "Select Subject") {
        emptyList()
    } else {
        mainAppUiState.attendance.filter { it.subject == selectedOption.value }
    }
    Log.d("ATTENDANCE",filteredAttendance.toString())
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val presentDatesLocal: List<LocalDate> = filteredAttendance
        .filter { it.status.equals("PRESENT", ignoreCase = true) }
        .map { LocalDate.parse(it.date, formatter) }

    val absentDatesLocal: List<LocalDate> = filteredAttendance
        .filter { it.status.equals("ABSENT", ignoreCase = true) }
        .map { LocalDate.parse(it.date, formatter) }

// If HolidayDTO.date is "yyyy-MM-dd" string:

    val holidayDatesLocal: List<LocalDate> =
        studentUiState.holidays.map { LocalDate.parse(it.date, formatter) }



    var currentMonth by remember { mutableStateOf("${LocalDate.now().month} ${LocalDate.now().year}") }

    val monthWisePresentDates by remember(currentMonth, presentDatesLocal) {
        derivedStateOf {
            if (currentMonth.isEmpty()) emptyList()
            else {
                val parts = currentMonth.split(" ")
                val monthEnum = Month.valueOf(parts[0].uppercase())
                val year = parts[1].toInt()

                presentDatesLocal.filter { it.month == monthEnum && it.year == year }
            }
        }
    }

    val monthWiseAbsentDates by remember(currentMonth, absentDatesLocal) {
        derivedStateOf {
            if (currentMonth.isEmpty()) emptyList()
            else {
                val parts = currentMonth.split(" ")
                val monthEnum = Month.valueOf(parts[0].uppercase())
                val year = parts[1].toInt()

                absentDatesLocal.filter { it.month == monthEnum && it.year == year }
            }
        }
    }

    val monthWiseHolidayDates by remember(currentMonth, holidayDatesLocal) {
        derivedStateOf {
            if (currentMonth.isEmpty()) emptyList()
            else {
                val parts = currentMonth.split(" ")
                val monthEnum = Month.valueOf(parts[0].uppercase())
                val year = parts[1].toInt()

                holidayDatesLocal.filter { it.month == monthEnum && it.year == year }
            }
        }
    }
    LaunchedEffect(Unit){
        mainAppViewModel.getAllSubjects(branch = userUiState.branch, semester = userUiState.semester)
        studentViewModel.getHolidaysServer()
    }





    LazyColumn(modifier = Modifier.padding(innerPadding)){
        item {

            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp,
                top = 10.dp, bottom = 10.dp)) {
                CustomDropdownMenu(options=options,
                    selectedOption = selectedOption){option->
                    selectedOption.value = option
                }
            }


        }
        item {
            if (selectedOption.value!="Select Subject") {
                Box(
                    modifier = Modifier
                        .padding(20.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0B2649),
                                    Color(0xFF3B4D6B)
                                )
                            ),
                            shape = RoundedCornerShape(30.dp)
                        )
                ){
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.SpaceBetween) {
                            Text("Current Attendance",
                                fontSize = 12.sp,
                                color = Color(0x9DFFFBFB),
                                modifier = Modifier.padding(bottom = 10.dp))
                            Text("${String.format("%.2f",(presentDatesLocal.size.toFloat() / filteredAttendance.size.toFloat())*100)}%", fontSize = 35.sp)
                        }
                        Column(modifier = Modifier.padding(22.dp),
                            verticalArrangement = Arrangement.SpaceBetween) {
                            Text("Classes Attended", fontSize = 12.sp,
                                color = Color(0x9DFFFBFB),
                                modifier = Modifier.padding(bottom = 10.dp))
                            Text(text = "${presentDatesLocal.size} of ${filteredAttendance.size}", fontSize = 30.sp,
                            modifier = Modifier.padding(end = 8.dp))
                        }
                    }
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .padding(20.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0B2649),
                                Color(0xFF3B4D6B)
                            )
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
            ){

                Column {
                    Row(modifier = Modifier.fillMaxWidth()
                        .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Attendance Calendar", fontSize = 25.sp,
                            modifier = Modifier.padding(6.dp),
                            fontWeight = FontWeight.ExtraBold)
                        val color = Color.Green

                    }
                    CalendarView(
                        presentDates = monthWisePresentDates,
                        absentDates = monthWiseAbsentDates,
                        holidays = monthWiseHolidayDates,
                        studentViewModel = studentViewModel,
                        studentUiState = studentUiState,
                        selectedMonth = { month -> currentMonth = month }
                    )

                }
            }
        }
    }
}

