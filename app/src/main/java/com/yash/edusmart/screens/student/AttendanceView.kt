package com.yash.edusmart.screens.student

import android.annotation.SuppressLint
import android.os.Build
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yash.edusmart.screens.component.CalendarView
import com.yash.edusmart.screens.component.CustomDropdownMenu
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
fun AttendanceView(innerPadding: PaddingValues,
                   studentUiState: StudentUiState,
                   studentViewModel: StudentViewModel,
                   userUiState: UserUiState){


    val options by remember(studentUiState.subjectList) {
        derivedStateOf {
            studentUiState.subjectList
                .filter { it != "BREAK" }   // remove BREAK
                .distinct()                 // keep only unique values
        }
    }



    var selectedOption by rememberSaveable { mutableStateOf("Select Subject") }

//    LaunchedEffect(studentUiState.selectedSubject) {
//        selectedOption = studentUiState.selectedSubject
//    }


//    LaunchedEffect(selectedOption){
//        studentViewModel.setSubject(selectedOption)
//    }


    val filteredAttendance by remember(studentUiState.attendance, selectedOption) {
        derivedStateOf {
            if (selectedOption == "Select Subject") emptyList()
            else studentUiState.attendance.filter { it.subject == selectedOption }
        }
    }

    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }

    val presentDatesLocal by remember(filteredAttendance) {
        derivedStateOf {
            filteredAttendance.asSequence()
                .filter { it.status.equals("PRESENT", true) }
                .mapNotNull { runCatching { LocalDate.parse(it.date, formatter) }.getOrNull() }
                .toList()
        }
    }

    val absentDatesLocal by remember(filteredAttendance) {
        derivedStateOf {
            filteredAttendance.asSequence()
                .filter { it.status.equals("ABSENT", true) }
                .mapNotNull { runCatching { LocalDate.parse(it.date, formatter) }.getOrNull() }
                .toList()
        }
    }

    val holidayDatesLocal by remember(studentUiState.holidays) {
        derivedStateOf {
            studentUiState.holidays.mapNotNull {
                runCatching { LocalDate.parse(it.date, formatter) }.getOrNull()
            }
        }
    }




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
        studentViewModel.getAllSubjects(branch = userUiState.branch, semester = userUiState.semester)
        studentViewModel.getHolidaysServer()
    }




    val pullState = rememberPullToRefreshState()
    PullToRefreshBox(
        state = pullState,
        onRefresh = {
            studentViewModel.getAttendance(
                email = userUiState.email
            )
        },
        modifier = Modifier.padding(innerPadding),
        isRefreshing = studentUiState.isLoading
    ) {
        LazyColumn {
            item {

                Column(
                    modifier = Modifier.padding(
                        start = 20.dp, end = 20.dp,
                        top = 10.dp, bottom = 10.dp
                    )
                ) {
                    CustomDropdownMenu(
                        options = options,
                        selectedOption = selectedOption
                    ) { option ->
                        selectedOption = option
                    }
                }


            }
            item {
                if (selectedOption != "Select Subject") {
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
                    ) {
                        val percentage =
                            if (filteredAttendance.isNotEmpty())
                                (presentDatesLocal.size.toFloat() / filteredAttendance.size.toFloat()) * 100f
                            else 0f
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Current Attendance",
                                    fontSize = 12.sp,
                                    color = Color(0x9DFFFBFB),
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )
                                Text(String.format("%.2f%%", percentage), fontSize = 35.sp)
                            }
                            Column(
                                modifier = Modifier.padding(22.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Classes Attended", fontSize = 12.sp,
                                    color = Color(0x9DFFFBFB),
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )
                                Text(
                                    text = "${presentDatesLocal.size} of ${filteredAttendance.size}",
                                    fontSize = 30.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
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
                ) {

                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Attendance Calendar", fontSize = 25.sp,
                                modifier = Modifier.padding(6.dp),
                                fontWeight = FontWeight.ExtraBold
                            )

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
}

