package com.yash.edusmart.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ChatEntries")
data class ChatEntries(
    @PrimaryKey(autoGenerate = true) val id: Long =0,
    val message: String,
    val isSent: Boolean,
    val sender: String,
    val receiver: String,
    val timeStamp: Long
)
