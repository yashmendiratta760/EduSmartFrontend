package com.yash.edusmart.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "Holidays")
data class Holidays (
   @PrimaryKey(autoGenerate = true) val id: Long,
    val date: LocalDate,
   val occasion: String
)