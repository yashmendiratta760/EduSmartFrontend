package com.yash.edusmart.viewmodel

import com.yash.edusmart.api.StudentData
import com.yash.edusmart.data.AssignmentGetDTO
import com.yash.edusmart.data.TimeTableEntry
import com.yash.edusmart.db.TimeTableDTO

data class TeacherUiState(
    val isLoading: Boolean=false,
    val callComplete: Boolean = false,
    val branch:List<String> = emptyList(),
    val assignments:List<AssignmentGetDTO> = emptyList(),
    val isChatListLoading: Boolean=false,
    val timeTableEntries:List<TimeTableDTO> = emptyList(),
    val studentDataChat:List<StudentData> = emptyList(),
    val studentDataAttendance:List<StudentData> = emptyList(),
    val timeTableTeacher:List<TimeTableEntry> = emptyList(),
)
