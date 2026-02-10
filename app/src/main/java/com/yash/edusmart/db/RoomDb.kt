package com.yash.edusmart.db

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDate

@TypeConverters(Converters::class, LocalDateConverter::class)
@Database(entities = [TimeTableEntries::class, ChatEntries::class, Assignments::class, Holidays::class], version = 11, exportSchema = false)
abstract class RoomDb: RoomDatabase()
{
    abstract fun timeTableDao(): TimeTableDao
    abstract fun chatDao(): ChatDao

    abstract fun assignmentDao(): AssignmentDao

    abstract fun HolidaysDao(): HolidaysDao

    companion object{

        @Volatile
        private var Instance: RoomDb?=null

        fun getDatabase(context: Context): RoomDb{
            return Instance?:synchronized(this){
                Room.databaseBuilder(context,RoomDb::class.java,"EduSmart")
                    .fallbackToDestructiveMigration()
                    .build().also { Instance=it }
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromList(list: List<String>): String = list.joinToString(",")

    @TypeConverter
    fun toList(data: String): List<String> =
        if (data.isEmpty()) emptyList() else data.split(",")
}


class LocalDateConverter {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString() // "yyyy-MM-dd"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }
}