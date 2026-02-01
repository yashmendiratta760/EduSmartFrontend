package com.yash.edusmart.viewmodel

import com.yash.edusmart.api.AttendanceDTO
import com.yash.edusmart.api.StudentData
import com.yash.edusmart.api.TeacherDTO
import com.yash.edusmart.data.AssignmentGetDTO
import com.yash.edusmart.data.TimeTableEntry
import com.yash.edusmart.db.Assignments
import com.yash.edusmart.db.TimeTableDTO
import com.yash.edusmart.db.TimeTableEntries

data class MainAppUiState(
    val timeTableEntries:List<TimeTableDTO> = emptyList(),
    val callComplete: Boolean = false,
    val attendance:List<AttendanceDTO> = emptyList(),
    var studentDataAttendance:List<StudentData> = emptyList(),
    var studentDataChat:List<StudentData> = emptyList(),
    val subjectList:List<String> = emptyList(),
    val branch:List<String> = emptyList(),
    val assignments:List<AssignmentGetDTO> = emptyList(),
    val timeTableTeacher:List<TimeTableEntry> = emptyList(),
    val teacher:List<TeacherDTO> = emptyList()
)
