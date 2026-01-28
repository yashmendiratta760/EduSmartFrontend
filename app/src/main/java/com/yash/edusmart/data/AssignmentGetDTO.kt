package com.yash.edusmart.data

data class AssignmentGetDTO(
    var id: Long,
    var assignment: String,
    var deadline: Long,
    var enroll: List<String>,
    var branch: String,
    var sem: String
)
