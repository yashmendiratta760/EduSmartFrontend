package com.yash.edusmart.screens.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yash.edusmart.viewmodel.StudentUiState
import com.yash.edusmart.viewmodel.StudentViewModel
import java.time.LocalDate
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    presentDates: List<LocalDate>,
    absentDates: List<LocalDate>,
    holidays: List<LocalDate>,
    selectedMonth: (String) -> Unit,
    studentUiState: StudentUiState,
    studentViewModel: StudentViewModel
) {
    var firstDayOfMonth by remember { mutableStateOf(studentUiState.selectedMonth) }

    LaunchedEffect(firstDayOfMonth) {
        studentViewModel.setMonth(firstDayOfMonth)
        selectedMonth("${firstDayOfMonth.month} ${firstDayOfMonth.year}")
    }

    // for O(1) lookups
    val presentSet = remember(presentDates) { presentDates.toSet() }
    val absentSet = remember(absentDates) { absentDates.toSet() }
    val holidaySet = remember(holidays) { holidays.toSet() }

    val daysInMonth = firstDayOfMonth.lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0

    Column(modifier = modifier.padding(16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                Icons.Default.ArrowBackIosNew,
                contentDescription = "Back Arrow",
                modifier = Modifier.clickable {
                    firstDayOfMonth = firstDayOfMonth.minusMonths(1)
                }
            )

            Text(
                text = "${firstDayOfMonth.month} ${firstDayOfMonth.year}",
                style = MaterialTheme.typography.titleLarge
            )

            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Forward Arrow",
                modifier = Modifier.clickable {
                    firstDayOfMonth = firstDayOfMonth.plusMonths(1)
                }
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                Text(text = it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(250.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            items(firstDayOfWeek) { Spacer(Modifier.size(40.dp)) }

            items((1..daysInMonth).toList()) { day ->
                val date = LocalDate.of(firstDayOfMonth.year, firstDayOfMonth.month, day)

                val isPresent = date in presentSet
                val isAbsent = date in absentSet
                val isHoliday = date in holidaySet

                val bg = when {
                    isPresent -> Color.Green.copy(alpha = 0.6f)
                    isAbsent -> Color.Red.copy(alpha = 0.6f)
                    isHoliday -> Color.Blue.copy(alpha = 0.7f)
                    else -> Color.Transparent
                }

                val textColor = when {
                    isPresent || isAbsent || isHoliday -> Color.Black
                    else -> MaterialTheme.colorScheme.onBackground
                }

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .background(bg, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = day.toString(), color = textColor)
                }
            }
        }

        // Legend (unchanged)
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .background(Color.Green.copy(alpha = 0.6f), CircleShape)
                )
                Text("Present", modifier = Modifier.padding(start = 8.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .background(Color.Blue.copy(alpha = 0.7f), CircleShape)
                )
                Text("Holiday", modifier = Modifier.padding(start = 8.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .background(Color.Red.copy(alpha = 0.8f), CircleShape)
                )
                Text("Absent", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
