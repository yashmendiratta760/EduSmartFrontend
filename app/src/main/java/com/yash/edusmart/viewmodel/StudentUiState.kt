package com.yash.edusmart.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import com.yash.edusmart.api.HolidayEntity
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
data class StudentUiState(
    val branch: String = "",
    val semester:Int = 0,
    val daySelected:String = LocalDate.now().dayOfWeek.name.lowercase()
        .replaceFirstChar { it.uppercase() },
    val selectedSubject: String = "Select Subject",
    val screenOpened:Int = 0,
    val selectedMonth: LocalDate = LocalDate.of(LocalDate.now().year, LocalDate.now().month, 1),
    val holidays:List<HolidayEntity> = emptyList()

)
