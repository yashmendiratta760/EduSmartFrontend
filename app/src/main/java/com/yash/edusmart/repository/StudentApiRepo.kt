package com.yash.edusmart.repository

import com.yash.edusmart.api.AssignmentStudent
import com.yash.edusmart.api.AttendanceDTO
import com.yash.edusmart.api.ChatEntity
import com.yash.edusmart.api.HolidayEntity
import com.yash.edusmart.api.StudentApi
import com.yash.edusmart.api.StudentData
import com.yash.edusmart.api.StudentsListDTO
import com.yash.edusmart.api.TeacherDTO
import com.yash.edusmart.data.TimeTableEntry
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface StudentApiRepo
{
    suspend fun getTimeTableByBranch(branch: String,semester: String): Response<List<TimeTableEntry>>
    suspend fun getAttendanceByEmail(email: String): Response<List<AttendanceDTO>>



    suspend fun getStudentsByBranchAndSemester(studentsListDTO : StudentsListDTO): Response<List<StudentData>>



    suspend fun getAllSubjects(branch: String,semester: String): Response<List<String>>







    suspend fun markAssignment(
        idAss: Long,
        enroll: String
    ): Response<String>

    suspend fun getAllAssign(
        branch: String,sem: String
    ): Response<List<AssignmentStudent>>

    suspend fun getHolidays(): Response<List<HolidayEntity>>





    suspend fun getStudentGroupMessages(
        branch: String,
        sem: String
    ): Response<List<ChatEntity>>

    suspend fun getStudentPrivateConversation(
        email: String,
        receiverEmail: String
    ): Response<List<ChatEntity>>

    suspend fun addPrivateMessage(
        chatEntity: ChatEntity
    ): Response<String>


    suspend fun getAllTeacher(
        branch: String, sem: String
    ): Response<List<TeacherDTO>>
}

class StudentApiRepoImpl @Inject constructor(private val studentApi: StudentApi): StudentApiRepo
{

    override suspend fun getTimeTableByBranch(
        branch: String,
        semester: String
    ): Response<List<TimeTableEntry>> {
        return studentApi.getTimeTableByBranch(branch,semester)
    }

    override suspend fun getAttendanceByEmail(email: String): Response<List<AttendanceDTO>> {
        return studentApi.getAttendance(email)
    }


    override suspend fun getStudentsByBranchAndSemester(studentsListDTO: StudentsListDTO): Response<List<StudentData>> {
        return studentApi.getStudentsByBranchAndSemester(studentsListDTO)
    }



    override suspend fun getAllSubjects(
        branch: String,
        semester: String
    ): Response<List<String>> {
        return studentApi.getSubjects(branch,semester)

    }


    override suspend fun markAssignment(
        idAss: Long,
        enroll: String
    ): Response<String> {
        return studentApi.markAssignment(idAss,enroll)
    }

    override suspend fun getAllAssign(branch: String,sem: String): Response<List<AssignmentStudent>> {
        return studentApi.getAllAssign(branch,sem)
    }

    override suspend fun getHolidays(): Response<List<HolidayEntity>> {
        return studentApi.getHolidays()
    }

    override suspend fun getStudentGroupMessages(
        branch: String,
        sem: String
    ): Response<List<ChatEntity>> {
        return studentApi.getGroupMessages(branch, sem)
    }

    override suspend fun getStudentPrivateConversation(
        email: String,
        receiverEmail: String
    ): Response<List<ChatEntity>> {
        return studentApi.getPrivateConversation(email, receiverEmail)
    }

    override suspend fun addPrivateMessage(
        chatEntity: ChatEntity
    ): Response<String> {
        return studentApi.addMsg(chatEntity)
    }

    override suspend fun getAllTeacher(
        branch: String,
        sem: String
    ): Response<List<TeacherDTO>> {
        return studentApi.getAllTeacher(branch,sem)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StudentApiRepoModule{
    @Binds
    @Singleton
    abstract fun joinImpl(impl: StudentApiRepoImpl): StudentApiRepo
}