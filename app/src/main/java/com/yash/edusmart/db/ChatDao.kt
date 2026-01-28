package com.yash.edusmart.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao{
    @Insert
    suspend fun insertEntry(data: ChatEntries)

    @Query("SELECT * FROM ChatEntries ORDER BY timeStamp")
     fun getMessages(): Flow<List<ChatEntries>>
}