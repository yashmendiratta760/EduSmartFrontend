package com.yash.edusmart.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Assignments")
data class Assignments(
    @PrimaryKey(autoGenerate = true) val id: Long=0,
    val branch: String,
    val sem: String,
    val enrollCom:List<String>,
    val task: String,
    val deadline: Long,
    val isCompleted: Boolean
)