package com.yash.edusmart.repository

import com.yash.edusmart.db.TimeTableDao
import com.yash.edusmart.db.TimeTableEntries
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

interface LocalDbRepo
{
    suspend fun getDataByBranchAndSemester(branch: String,semester: Int):List<TimeTableEntries>
    suspend fun insert(item: TimeTableEntries)
    suspend fun update(item: TimeTableEntries)

    suspend fun deleteAll()
}
class LocalDbRepoImpl @Inject constructor(private val timeTableDao: TimeTableDao): LocalDbRepo{
    override suspend fun getDataByBranchAndSemester(branch: String,semester: Int): List<TimeTableEntries> {
        return timeTableDao.getDataByBranchAndSemester(branch, semester)
    }


    override suspend fun insert(item: TimeTableEntries) {
        return timeTableDao.insertEntry(item)
    }

    override suspend fun update(item: TimeTableEntries) {
        return timeTableDao.updateEntry(item)
    }

    override suspend fun deleteAll() {
        return timeTableDao.deleteAll()
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDbRepoModule{
    @Binds
    @Singleton
    abstract fun bindImplOfLocalDb(impl: LocalDbRepoImpl): LocalDbRepo
}