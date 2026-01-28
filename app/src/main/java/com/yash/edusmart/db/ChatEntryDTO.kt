package com.yash.edusmart.db

data class ChatEntryDTO(
    val sender: String,
    val receiver: String,
    val timeStamp: String,
    val isSent: Boolean
)
