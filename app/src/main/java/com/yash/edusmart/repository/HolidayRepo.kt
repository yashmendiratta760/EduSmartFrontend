package com.yash.edusmart.repository

import com.yash.edusmart.db.Holidays
import com.yash.edusmart.db.HolidaysDao
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

interface HolidayRepo {
    suspend fun getAll(): List<Holidays>
}

class HolidayRepoImpl @Inject constructor(private val holidaysDao: HolidaysDao): HolidayRepo{
    override suspend fun getAll(): List<Holidays> {
        return holidaysDao.getAll()
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class HoliRepo{
    @Binds
    @Singleton
    abstract fun provideRepoHoli(repo: HolidayRepoImpl): HolidayRepo
}