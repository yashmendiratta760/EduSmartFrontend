package com.yash.edusmart.screens.student

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yash.edusmart.db.Assignments
import com.yash.edusmart.screens.component.CustomDropdownMenu
import com.yash.edusmart.screens.component.student.AttendanceAlert
import com.yash.edusmart.screens.component.student.ClassCountDown
import com.yash.edusmart.screens.component.student.TaskAlert
import com.yash.edusmart.screens.getBoxColor
import com.yash.edusmart.viewmodel.ChatUiState
import com.yash.edusmart.viewmodel.StudentUiState
import com.yash.edusmart.viewmodel.StudentViewModel
import com.yash.edusmart.viewmodel.UserUiState
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(innerPadding: PaddingValues,
               selectedDay: MutableState<String>,
               chatUiState: ChatUiState,
               studentUiState: StudentUiState,
               studentViewModel: StudentViewModel,
               userUiState: UserUiState,
               selectedIndexSend:(Int)->Unit){


    val assigns = remember { mutableStateListOf<Assignments>() }

    LaunchedEffect(chatUiState.assignments) {
        assigns.clear()
        assigns.addAll(chatUiState.assignments)
    }



    val totalAttendance by remember(studentUiState.attendance) {
        derivedStateOf { studentUiState.attendance.size }
    }

    val presentAttendance by remember(studentUiState.attendance) {
        derivedStateOf { studentUiState.attendance.count { it.status.equals("PRESENT", true) } }
    }

    val attendancePercent by remember(totalAttendance, presentAttendance) {
        derivedStateOf {
            if (totalAttendance > 0) ((presentAttendance.toFloat() / totalAttendance) * 100f).let { "%.2f".format(it).toFloat() }
            else 0f
        }
    }



    val formatterTime = remember { DateTimeFormatter.ofPattern("HH:mm") }

    val sortedEntries by remember(studentUiState.timeTableEntries, selectedDay.value) {
        derivedStateOf {
            studentUiState.timeTableEntries
                .filter { it.day.equals(selectedDay.value, ignoreCase = true) }
                .sortedBy { entry ->
                    runCatching {
                        val start = entry.timing.substringBefore("-").trim()
                        LocalTime.parse(start, formatterTime)
                    }.getOrElse { LocalTime.MAX }
                }
        }
    }

    val classesCompleted by remember(sortedEntries) {
        derivedStateOf {
            val now = LocalTime.now()
            sortedEntries.count { entry ->
                val endStr = entry.timing.substringAfter("-", "").trim()
                val end = runCatching { LocalTime.parse(endStr, formatterTime) }.getOrNull()
                end != null && now.isAfter(end)
            }
        }
    }

    val pullState = rememberPullToRefreshState()
    PullToRefreshBox(
        state = pullState,
        onRefresh = {
            studentViewModel.getAssignmentStudent(userUiState.branch,userUiState.semester)
            studentViewModel.getAttendance(userUiState.email)
        },
        modifier = Modifier.padding(innerPadding),
        isRefreshing = studentUiState.isLoading
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(
                    modifier = Modifier
                        .padding(20.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(color = 0xFF0F2E55).copy(alpha = 0.5f),
                                    Color(0xFF1F4B80)
                                )
                            ),
                            shape = RoundedCornerShape(30.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(15.dp)) {
                        Text(
                            text = "Your time table",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 30.sp
                        )

                        CustomDropdownMenu(
                            options = listOf(
                                "Sunday", "Monday", "Tuesday", "Wednesday",
                                "Thursday", "Friday", "Saturday"
                            ),
                            selectedOption = selectedDay.value
                        ) { option ->
                            selectedDay.value = option
                        }

                        // Table header row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Time", fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 25.dp)
                            )
                            Text(
                                text = "Subject", fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 25.dp)
                            )
                        }
                        Divider(thickness = 1.dp)


                        Column {
                            sortedEntries
                                .forEach { entry ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = entry.timing)
                                        val color = getBoxColor(entry.subject, entry.timing)
                                        Box(
                                            modifier = Modifier
                                                .border(
                                                    width = 2.dp,
                                                    color = color,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .background(
                                                    color.copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(8.dp)
                                                ), contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = entry.subject+"(#${entry.room})",
                                                modifier = Modifier.padding(6.dp),
                                                fontSize = if (entry.subject.length > 21) 12.sp else 15.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(15.dp))
                                }
                        }
                    }
                }
            }
            item {
                Column {
                    ClassCountDown(
                        classesCompleted = classesCompleted,
                        totalClasses = sortedEntries.size
                    )
                    AttendanceAlert(
                        attendance = attendancePercent,
                        onClick = {
                            selectedIndexSend(1)
                        }
                    )
                    val topThreeTasks = assigns.take(3)
                        .mapIndexed { index, assignment ->
                            "${index + 1}. ${assignment.task}"
                        }

                    TaskAlert(
                        heading = "Assignments",
                        task = topThreeTasks.joinToString("\n"),
                        isTeacher = false,
                        isStudent = false,
                        ifHomeScreen = true
                    )
                }
            }


        }
    }

}