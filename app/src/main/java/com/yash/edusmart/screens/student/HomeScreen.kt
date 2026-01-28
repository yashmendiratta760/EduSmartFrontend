package com.yash.edusmart.screens.student

import android.net.wifi.hotspot2.pps.HomeSp
import android.os.Build
import android.util.Log
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yash.edusmart.api.AttendanceDTO
import com.yash.edusmart.db.Assignments
import com.yash.edusmart.screens.component.CustomDropdownMenu
import com.yash.edusmart.screens.component.student.AttendanceAlert
import com.yash.edusmart.screens.component.student.ClassCountDown
import com.yash.edusmart.screens.component.student.TaskAlert
import com.yash.edusmart.screens.getBoxColor
import com.yash.edusmart.viewmodel.ChatUiState
import com.yash.edusmart.viewmodel.MainAppUiState
import com.yash.edusmart.viewmodel.StudentUiState
import com.yash.edusmart.viewmodel.StudentViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(innerPadding: PaddingValues,
               selectedDay: MutableState<String>,
               mainAppUiState: MainAppUiState,
               chatUiState: ChatUiState,
               selectedIndexSend:(Int)->Unit){


    var assigns by remember {
        mutableStateOf<List<Assignments>>(emptyList())
    }

    LaunchedEffect(chatUiState.assignments) {
        assigns=chatUiState.assignments
    }

    val dates = mainAppUiState.attendance.map { Triple(it.date, it.status, it.subject) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val presentDatesLocal: List<LocalDate> = dates
        .filter { it.second.equals("PRESENT", ignoreCase = true) }
        .map { LocalDate.parse(it.first, formatter) }


    val formatterTime = DateTimeFormatter.ofPattern("HH:mm")

    val sortedEntries = mainAppUiState.timeTableEntries
        .filter { it.day.uppercase() == selectedDay.value.uppercase() }
        .sortedBy { entry ->
            try {
                val startTime = entry.timing.split("-").first().trim()
                LocalTime.parse(startTime, formatterTime)
            } catch (e: Exception) {
                LocalTime.MAX // fallback: put unparsable timings at the end
            }
        }
    Log.d("timetable",sortedEntries.firstOrNull()?.subject ?:"Nu")


        LazyColumn (modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally){
            item {
                Box(
                    modifier = Modifier
                        .padding(20.dp)
                        .background(
                            brush = Brush.linearGradient(colors = listOf(Color(color = 0xFF0F2E55).copy(alpha = 0.5f),
                                Color(0xFF1F4B80)
                            )),
                            shape = RoundedCornerShape(30.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(15.dp)) {
                        Text(text = "Your time table",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 30.sp)

                        CustomDropdownMenu(
                            options = listOf(
                                "Sunday", "Monday", "Tuesday", "Wednesday",
                                "Thursday", "Friday", "Saturday"
                            ),
                            selectedOption = selectedDay
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
                            Text(text = "Time", fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 25.dp))
                            Text(text = "Subject", fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 25.dp))
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
                                        val color = getBoxColor(entry.subject,entry.timing)
                                        Box(modifier = Modifier
                                            .border(width = 2.dp,
                                                color = color,
                                                shape = RoundedCornerShape(8.dp))
                                            .background(color.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(8.dp))
                                            ,contentAlignment = Alignment.Center
                                        ){
                                            Text(text = entry.subject,
                                                modifier = Modifier.padding(6.dp),
                                                fontSize = if(entry.subject.length>21) 12.sp else 15.sp)
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
                    var classesCompleted = 0
                    sortedEntries
                        .forEach { entry ->
                            val idx = entry.timing.indexOf("-")
                            if (idx != -1) {
                                val endTimeStr = entry.timing.substring(idx + 1).trim() // "10:30"
                                try {
                                    val endTime = LocalTime.parse(endTimeStr, formatterTime)
                                    val currTime = LocalTime.now()

                                    if (currTime.isAfter(endTime)) {
                                        classesCompleted++
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    ClassCountDown(classesCompleted = classesCompleted,
                        totalClasses = sortedEntries.size)
                    AttendanceAlert(attendance =
                        if (dates.size!=0){
                            (presentDatesLocal.size.toFloat()/dates.size.toFloat())*100
                        }
                        else 0f
                        ,
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
                        deadline = "",
                        isStudent = false
                    )
                }
            }


        }

}