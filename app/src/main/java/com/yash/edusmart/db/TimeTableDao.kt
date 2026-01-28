package com.yash.edusmart.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TimeTableDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(data: TimeTableEntries)

    @Update
    suspend fun updateEntry(data: TimeTableEntries)

    @Query("select * from timetableentity where branch = :branch and semester = :semester")
    suspend fun getDataByBranchAndSemester(branch: String,semester: Int):List<TimeTableEntries>
}

