package com.yash.edusmart.viewmodel

import com.yash.edusmart.db.Assignments
import com.yash.edusmart.db.ChatEntries

data class ChatUiState(
    var existingEntries:List<ChatEntries> = emptyList(),
    var assignments:List<Assignments> = emptyList(),
    var branch: String = "",
    val sem: String=""
)
