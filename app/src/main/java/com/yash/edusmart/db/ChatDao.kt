package com.yash.edusmart.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao{
    @Insert
    suspend fun insertEntry(data: ChatEntries)

    @Query("SELECT * FROM ChatEntries ORDER BY timeStamp")
     fun getMessages(): Flow<List<ChatEntries>>
    @Query("delete from chatentries")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertAll(list: List<ChatEntries>)

    @Query("DELETE FROM chatentries WHERE receiver = :receiver AND id NOT IN (:serverIds)")
    suspend fun deleteNotInServer(receiver: String, serverIds: List<Long>)

    @Query("DELETE FROM chatentries WHERE receiver = :receiver")
    suspend fun deleteByReceiver(receiver: String)


}