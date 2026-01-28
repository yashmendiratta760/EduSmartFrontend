package com.yash.edusmart.data

data class AttendanceUploadDTO(
    val attendance : List<AttendanceStatus>,
    val subjectName: String,
    val time: String,
    val branch: String,
    val semester: Int,
    val date: String
)
