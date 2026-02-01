package com.yash.edusmart.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface HolidaysDao {
    @Query("Select * from holidays")
    suspend fun getAll():List<Holidays>

    @Query("delete from holidays")
    suspend fun deleteAll()
}