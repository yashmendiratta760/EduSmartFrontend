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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yash.edusmart.screens.component.CustomDropdownMenu
import com.yash.edusmart.screens.getBoxColor
import com.yash.edusmart.viewmodel.TeacherUiState
import com.yash.edusmart.viewmodel.TeacherViewModel
import com.yash.edusmart.viewmodel.UserUiState
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeTableTeacher(innerPadding: PaddingValues,
                     selectedDay: MutableState<String>,
                     teacherViewModel: TeacherViewModel,
                     userUiState: UserUiState,
                     teacherUiState: TeacherUiState){
    val formatterTime = remember { DateTimeFormatter.ofPattern("HH:mm") }
//    LaunchedEffect(mainAppUiState.timeTableTeacher) {
//        mainAppViewModel.getTimeTableTeacher(userUiState.email)
//    }
    val sortedEntries = remember(teacherUiState.timeTableTeacher,selectedDay.value) {
        teacherUiState.timeTableTeacher
        .filter { it.day.uppercase() == selectedDay.value.uppercase() }
        .sortedBy { entry ->
            try {
                val startTime = entry.time.split("-").first().trim()
                LocalTime.parse(startTime, formatterTime)
            } catch (e: Exception) {
                LocalTime.MAX // fallback: put unparsable timings at the end
            }
        }.distinct()
    }

    val pullState = rememberPullToRefreshState()
    PullToRefreshBox(
        state = pullState,
        onRefresh = {
                teacherViewModel.getTimeTableTeacher()
        },
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        isRefreshing = teacherUiState.isLoading
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Time", fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 25.dp)
                            )
                            Text(
                                text = "Branch", fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 25.dp),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Subject", fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 25.dp),
                                textAlign = TextAlign.End
                            )
                        }
                        Divider(thickness = 1.dp)



                            sortedEntries
                                .forEach { entry ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = entry.time,
                                            modifier = Modifier.weight(1.5f),  // give it more width
                                            maxLines = 1,                      // force single line
                                            softWrap = false                   // never wrap
                                        )

                                        Text(
                                            entry.branch,
                                            modifier = Modifier.weight(1f),
                                            textAlign = TextAlign.Center
                                        )

                                        val color = getBoxColor(entry.subject, entry.time)
                                        Box(
                                            modifier = Modifier
                                                .weight(2f)   // fixed column width
                                                .padding(start = 8.dp)
                                                .border(2.dp, color, RoundedCornerShape(8.dp))
                                                .background(color.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = entry.subject+"(#${entry.room})",
                                                modifier = Modifier.padding(6.dp),
                                                maxLines = 3,          // up to 3 lines
                                                softWrap = true,       // allow wrapping
                                                fontSize = 15.sp
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
