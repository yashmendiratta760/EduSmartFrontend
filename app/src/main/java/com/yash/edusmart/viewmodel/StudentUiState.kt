package com.yash.edusmart.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import com.yash.edusmart.api.AttendanceDTO
import com.yash.edusmart.api.HolidayEntity
import com.yash.edusmart.api.StudentData
import com.yash.edusmart.api.TeacherDTO
import com.yash.edusmart.db.TimeTableDTO
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
data class StudentUiState(
    val holidays:List<HolidayEntity> = emptyList(),
    val teacher:List<TeacherDTO> = emptyList(),
    val callComplete: Boolean = false,
    val timeTableEntries:List<TimeTableDTO> = emptyList(),
    val attendance:List<AttendanceDTO> = emptyList(),
    val isLoading: Boolean = false,
    val isChatListLoading: Boolean=false,
    var studentDataChat:List<StudentData> = emptyList(),
    val subjectList:List<String> = emptyList()
)
