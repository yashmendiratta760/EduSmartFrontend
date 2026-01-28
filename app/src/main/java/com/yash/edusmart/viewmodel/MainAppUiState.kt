package com.yash.edusmart.viewmodel

import com.yash.edusmart.api.AttendanceDTO
import com.yash.edusmart.api.StudentData
import com.yash.edusmart.data.AssignmentGetDTO
import com.yash.edusmart.db.Assignments
import com.yash.edusmart.db.TimeTableDTO

data class MainAppUiState(
    val timeTableEntries:List<TimeTableDTO> = emptyList(),
    val callComplete: Boolean = false,
    val attendance:List<AttendanceDTO> = emptyList(),
    var studentDataAttendance:List<StudentData> = emptyList(),
    var studentDataChat:List<StudentData> = emptyList(),
    val subjectList:List<String> = emptyList(),
    val branch:List<String> = emptyList(),
    val assignments:List<AssignmentGetDTO> = emptyList()
)
