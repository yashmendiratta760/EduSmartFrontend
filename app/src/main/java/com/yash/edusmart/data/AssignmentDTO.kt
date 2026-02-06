package com.yash.edusmart.data

data class AssignmentDTO(
    val sender: String,
    val receiver: String,
    val task: String,
    val deadline: Long,
    val path: String
)
