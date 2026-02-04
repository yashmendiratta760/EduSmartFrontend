package com.yash.edusmart.api

import com.yash.edusmart.data.TimeTableEntry
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface StudentApi
{

    @GET("/student/getHolidays")
    suspend fun getHolidays(): Response<List<HolidayEntity>>
    @GET("/student/getTimeTableByDay")
    suspend fun getTimeTableByBranch(
        @Query("branch") branch: String,
        @Query("semester") semester: String
    ): Response<List<TimeTableEntry>>

    @GET("/student/getAttendance")
    suspend fun getAttendance(
        @Query("email") email: String
    ): Response<List<AttendanceDTO>>

    @POST("/student/getStudentsList")
    suspend fun getStudentsByBranchAndSemester(
        @Body studentsListDTO : StudentsListDTO
    ): Response<List<StudentData>>

    @GET("/student/getAllSubjects")
    suspend fun getSubjects(
        @Query("branch") branch: String,
        @Query("semester")semester: String
    ): Response<List<String>>

    @GET("/student/getAllAssignmentsByBranchAndSem")
    suspend fun getAllAssign(
        @Query("branch") branch: String,
        @Query("sem") sem: String
    ): Response<List<AssignmentStudent>>

    @POST("/student/markAssignment")
    suspend fun markAssignment(
        @Query("idAss") idAss: Long,
        @Query("enroll")enroll: String
    ): Response<String>

    @GET("/student/getMessagesByBranchAndSem")
    suspend fun getGroupMessages(
        @Query("branch") branch: String,
        @Query("sem") sem: String
    ): Response<List<ChatEntity>>

    @GET("/student/getPvtMsg")
    suspend fun getPrivateConversation(
        @Query("email") email: String,
        @Query("receiverEmail") receiverEmail: String
    ): Response<List<ChatEntity>>

    @PUT("/student/addPvtMsg")
    suspend fun addMsg(
        @Body chatEntity: ChatEntity
    ): Response<String>

    @GET("/student/getAllTeachers")
    suspend fun getAllTeacher(
        @Query("branch") branch: String,
        @Query("sem") sem: String
    ): Response<List<TeacherDTO>>






}
data class TeacherDTO(
    val name: String,
    val email: String
)

data class ChatEntity(
    val id: Long? = null,
    val msg: String,
    val isSent: Boolean,
    val sender: String,
    val receiver: String,
    val timeStamp: Long
)


data class HolidayEntity(
    val id: Long,
    val date : String,
    val occasion: String
)

data class AssignmentStudent(
    val id: Long,
    val branch: String,
    val sem: String,
    val assignment: String,
    val deadline: Long
)

data class StudentData(
    val email: String,
    val name: String
)

data class StudentsListDTO(
    val branch: String,
    val semester: String
)

data class AttendanceDTO(
    val date: String,
    val status: String,
    val subject: String
)