package com.yash.edusmart.repository

import com.yash.edusmart.db.AssignmentDao
import com.yash.edusmart.db.Assignments
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface AssignmentLocalRepo
{
    suspend fun insert(assignments: Assignments)

    suspend fun getTasks(): Flow<List<Assignments>>

    suspend fun findMatching(
        branch: String,
        sem: String,
        task: String,
        deadline: Long
    ): Assignments?
    suspend fun updateIdAndEnroll(oldId: Long, newId: Long, newEnrollCom: List<String>)

    suspend fun updateEnrollOnly(id: Long, newEnrollCom: List<String>)

    suspend fun updateIsCompleted(id: Long,isCompleted: Boolean)

    suspend fun deleteExtras(validIds: List<Long>)
}
class AssignmentLocalRepoImpl @Inject constructor(private val assignmentDao: AssignmentDao):
    AssignmentLocalRepo{
    override suspend fun insert(assignments: Assignments) {
        return assignmentDao.insert(assignments)
    }

    override suspend fun getTasks(

    ): Flow<List<Assignments>> {
        return assignmentDao.getTasks()
    }

    override suspend fun findMatching(
        branch: String,
        sem: String,
        task: String,
        deadline: Long
    ): Assignments? {
        return assignmentDao.findMatching(branch,sem,task,deadline)
    }

    override suspend fun updateIdAndEnroll(
        oldId: Long,
        newId: Long,
        newEnrollCom: List<String>
    ) {
        return assignmentDao.updateIdAndEnroll(oldId,newId,newEnrollCom)
    }

    override suspend fun updateEnrollOnly(id: Long, newEnrollCom: List<String>) {
        return assignmentDao.updateEnrollOnly(id,newEnrollCom)
    }

    override suspend fun updateIsCompleted(id: Long,isCompleted: Boolean) {
        return assignmentDao.updateIsCompleted(id,isCompleted)
    }

    override suspend fun deleteExtras(validIds: List<Long>) {
        return assignmentDao.deleteExtras(validIds)
    }


}

@Module
@InstallIn(SingletonComponent::class)
abstract class assRepo{
    @Binds
    @Singleton
    abstract fun provideRepo(repo: AssignmentLocalRepoImpl): AssignmentLocalRepo
}