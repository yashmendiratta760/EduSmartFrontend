package com.yash.edusmart.screens.teacher

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yash.edusmart.screens.component.CustomDropdownMenu
import com.yash.edusmart.screens.getBoxColor
import com.yash.edusmart.viewmodel.MainAppUiState
import com.yash.edusmart.viewmodel.MainAppViewModel
import com.yash.edusmart.viewmodel.UserUiState
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeTableTeacher(innerPadding: PaddingValues,
                     selectedDay: MutableState<String>,
                     mainAppUiState: MainAppUiState,
                     mainAppViewModel: MainAppViewModel,
                     userUiState: UserUiState){
    val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
//    LaunchedEffect(mainAppUiState.timeTableTeacher) {
//        mainAppViewModel.getTimeTableTeacher(userUiState.email)
//    }
    val sortedEntries = mainAppUiState.timeTableTeacher
        .filter { it.day.uppercase() == selectedDay.value.uppercase() }
        .sortedBy { entry ->
            try {
                val startTime = entry.time.split("-").first().trim()
                LocalTime.parse(startTime, formatterTime)
            } catch (e: Exception) {
                LocalTime.MAX // fallback: put unparsable timings at the end
            }
        }.distinct()

    LazyColumn (modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
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
                        Text(
                            text = "Time", fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 25.dp)
                        )
                        Text(
                            text = "Branch", fontWeight = FontWeight.Bold,
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
                                    Text(text = entry.time)
                                    Text(text = entry.branch)
                                    val color = getBoxColor(entry.subject, entry.time)
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
                                            text = entry.subject,
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
    }
}
