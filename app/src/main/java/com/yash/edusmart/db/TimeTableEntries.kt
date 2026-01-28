package com.yash.edusmart.db

import androidx.compose.runtime.internal.StabilityInferred
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TimeTableEntity")
data class TimeTableEntries(
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    val day: String,
    val subject: String,
    val timing:String,
    val branch: String,
    val semester : Int
)