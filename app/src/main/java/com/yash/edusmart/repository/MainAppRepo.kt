package com.yash.edusmart.repository

import com.yash.edusmart.api.AssignmentStudent
import com.yash.edusmart.api.AttendanceDTO
import com.yash.edusmart.api.ChatEntity
import com.yash.edusmart.api.HolidayEntity
import com.yash.edusmart.api.MainAppApi
import com.yash.edusmart.api.StudentData
import com.yash.edusmart.api.StudentsListDTO
import com.yash.edusmart.api.TeacherDTO
import com.yash.edusmart.data.AssignmentGetDTO
import com.yash.edusmart.data.AttendanceUploadDTO
import com.yash.edusmart.data.TimeTableEntry
import com.yash.edusmart.db.TimeTableEntries
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

interface MainAppRepo
{
    suspend fun getTimeTableByBranch(branch: String,semester: String): Response<List<TimeTableEntry>>
    suspend fun getAttendanceByEmail(email: String): Response<List<AttendanceDTO>>

    suspend fun uploadAttendance(attendanceUploadDTO: AttendanceUploadDTO): Response<String>

    suspend fun getStudentsByBranchAndSemester(studentsListDTO : StudentsListDTO): Response<List<StudentData>>

    suspend fun getStudentsByBranchAndSemesterTeacher(studentsListDTO : StudentsListDTO): Response<List<StudentData>>

    suspend fun getAllSubjects(branch: String,semester: String): Response<List<String>>

    suspend fun getAllBranch(): Response<List<String>>

    suspend fun getTimeTableByBranchAndSemesterTeacher(branch: String,semester: String): Response<List<TimeTableEntry>>

    suspend fun getAllAssignTeacher(): Response<List<AssignmentGetDTO>>

    suspend fun markAssignment(
        idAss: Long,
        enroll: String
    ): Response<String>

    suspend fun getAllAssign(): Response<List<AssignmentStudent>>

    suspend fun getHolidays(): Response<List<HolidayEntity>>

    suspend fun getTeacherTimeTable(email : String): Response<List<TimeTableEntry>>

    suspend fun deleteAssignment(id: Long): Response<String>

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


    /* ---------------- TEACHER ---------------- */

    suspend fun getTeacherGroupMessages(
        email: String,
        branch: String,
        sem: String
    ): Response<List<ChatEntity>>

    suspend fun getTeacherPrivateConversation(
        email: String,
        receiverEmail: String
    ): Response<List<ChatEntity>>

    suspend fun getAllTeacher(
        branch: String, sem: String
    ): Response<List<TeacherDTO>>
}

class MainAppRepoImpl @Inject constructor(private val mainAppApi: MainAppApi): MainAppRepo
{

    override suspend fun getTimeTableByBranch(
        branch: String,
        semester: String
    ): Response<List<TimeTableEntry>> {
        return mainAppApi.getTimeTableByBranch(branch,semester)
    }

    override suspend fun getAttendanceByEmail(email: String): Response<List<AttendanceDTO>> {
        return mainAppApi.getAttendance(email)
    }

    override suspend fun uploadAttendance(attendanceUploadDTO: AttendanceUploadDTO): Response<String> {
        return mainAppApi.uploadAttendance(attendanceUploadDTO)
    }

    override suspend fun getStudentsByBranchAndSemester(studentsListDTO: StudentsListDTO): Response<List<StudentData>> {
        return mainAppApi.getStudentsByBranchAndSemester(studentsListDTO)
    }

    override suspend fun getStudentsByBranchAndSemesterTeacher(studentsListDTO: StudentsListDTO): Response<List<StudentData>> {
        return mainAppApi.getStudentsByBranchAndSemesterTeacher(studentsListDTO)
    }

    override suspend fun getAllSubjects(
        branch: String,
        semester: String
    ): Response<List<String>> {
        return mainAppApi.getSubjects(branch,semester)

    }

    override suspend fun getAllBranch(): Response<List<String>> {
        return mainAppApi.getAllBranch()
    }

    override suspend fun getTimeTableByBranchAndSemesterTeacher(
        branch: String,
        semester: String
    ): Response<List<TimeTableEntry>> {
        return mainAppApi.getTimeTableByBranchAndSemesterTeacher(branch,semester)
    }

    override suspend fun getAllAssignTeacher(): Response<List<AssignmentGetDTO>> {
        return mainAppApi.getAllAssignTeacher()
    }

    override suspend fun markAssignment(
        idAss: Long,
        enroll: String
    ): Response<String> {
        return mainAppApi.markAssignment(idAss,enroll)
    }

    override suspend fun getAllAssign(): Response<List<AssignmentStudent>> {
        return mainAppApi.getAllAssign()
    }

    override suspend fun getHolidays(): Response<List<HolidayEntity>> {
        return mainAppApi.getHolidays()
    }

    override suspend fun getTeacherTimeTable(email: String): Response<List<TimeTableEntry>> {
        return mainAppApi.getTeacherTimeTable(email)
    }

    override suspend fun deleteAssignment(id: Long): Response<String> {
        return mainAppApi.deleteAssignment(id)
    }

    override suspend fun getStudentGroupMessages(
        branch: String,
        sem: String
    ): Response<List<ChatEntity>> {
        return mainAppApi.getGroupMessages(branch, sem)
    }

    override suspend fun getStudentPrivateConversation(
        email: String,
        receiverEmail: String
    ): Response<List<ChatEntity>> {
        return mainAppApi.getPrivateConversation(email, receiverEmail)
    }

    override suspend fun addPrivateMessage(
        chatEntity: ChatEntity
    ): Response<String> {
        return mainAppApi.addMsg(chatEntity)
    }


    /* ---------------- TEACHER ---------------- */

    override suspend fun getTeacherGroupMessages(
        email: String,
        branch: String,
        sem: String
    ): Response<List<ChatEntity>> {
        return mainAppApi.getGroupMessagesTeacher(email, branch, sem)
    }

    override suspend fun getTeacherPrivateConversation(
        email: String,
        receiverEmail: String
    ): Response<List<ChatEntity>> {
        return mainAppApi.getPrivateConversationTeacher(email, receiverEmail)
    }

    override suspend fun getAllTeacher(
        branch: String,
        sem: String
    ): Response<List<TeacherDTO>> {
        return mainAppApi.getAllTeacher(branch,sem)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class MainAppRepoModule{
    @Binds
    @Singleton
    abstract fun joinImpl(impl: MainAppRepoImpl): MainAppRepo
}