package com.yash.edusmart.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao
{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(data: Assignments)

    @Query("SELECT * FROM Assignments")
     fun getTasks(): Flow<List<Assignments>>

     @Query("update assignments set isCompleted=:isCompleted where id=:id ")
    suspend fun updateIsCompleted(id: Long,isCompleted: Boolean)

    @Query("""
    SELECT * FROM assignments 
    WHERE branch = :branch 
    AND sem = :sem
    AND task = :task 
    AND deadline = :deadline 
    LIMIT 1
    """)
    suspend fun findMatching(
        branch: String,
        sem: String,
        task: String,
        deadline: Long
    ): Assignments?

        @Query("""
    UPDATE assignments 
    SET id = :newId, enrollCom = :newEnrollCom 
    WHERE id = :oldId
    """)
    suspend fun updateIdAndEnroll(oldId: Long, newId: Long, newEnrollCom: List<String>)

    @Query("""
    UPDATE assignments 
    SET enrollCom = :newEnrollCom 
    WHERE id = :id
    """)
    suspend fun updateEnrollOnly(id: Long, newEnrollCom: List<String>)

    @Query("DELETE FROM assignments WHERE id NOT IN (:validIds)")
    suspend fun deleteExtras(validIds: List<Long>)

    @Query("delete from assignments")
    suspend fun deleteAll()

}