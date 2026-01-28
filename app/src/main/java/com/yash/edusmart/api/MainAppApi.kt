package com.yash.edusmart.api

import android.icu.util.LocaleData
import androidx.compose.ui.graphics.LinearGradient
import com.yash.edusmart.data.AssignmentGetDTO
import com.yash.edusmart.data.AttendanceUploadDTO
import com.yash.edusmart.data.TimeTableEntry
import com.yash.edusmart.db.Assignments
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.time.LocalDate
import java.util.Locale

interface MainAppApi
{

    @GET("/student/getHolidays")
    suspend fun getHolidays(): Response<List<HolidayEntity>>
    @GET("/student/getTimeTableByDay")
    suspend fun getTimeTableByBranch(
        @Query("branch") branch: String,
        @Query("semester") semester: String
    ): Response<List<TimeTableEntry>>

    @GET("/teacher/getTimeTableByDay")
    suspend fun getTimeTableByBranchAndSemesterTeacher(
        @Query("branch") branch: String,
        @Query("semester") semester: String
    ): Response<List<TimeTableEntry>>

    @GET("/student/getAttendance")
    suspend fun getAttendance(
        @Query("email") email: String
    ): Response<List<AttendanceDTO>>

    @POST("/teacher/uploadAttendance")
    suspend fun uploadAttendance(
        @Body attendanceUploadDTO: AttendanceUploadDTO
    ): Response<String>

    @POST("/student/getStudentsList")
    suspend fun getStudentsByBranchAndSemester(
        @Body studentsListDTO : StudentsListDTO
    ): Response<List<StudentData>>


    @POST("/teacher/getStudentsListTeacher")
    suspend fun getStudentsByBranchAndSemesterTeacher(
        @Body studentsListDTO : StudentsListDTO
    ): Response<List<StudentData>>

    @GET("/student/getAllSubjects")
    suspend fun getSubjects(
        @Query("branch") branch: String,
        @Query("semester")semester: String
    ): Response<List<String>>

    @GET("/teacher/getAllBranch")
    suspend fun getAllBranch(): Response<List<String>>

    @GET("/teacher/getAllAssignments")
    suspend fun getAllAssignTeacher(): Response<List<AssignmentGetDTO>>

    @GET("/student/getAssignment")
    suspend fun getAllAssign(): Response<List<AssignmentStudent>>

    @POST("/student/markAssignment")
    suspend fun markAssignment(
        @Query("idAss") idAss: Long,
        @Query("enroll")enroll: String
    ): Response<String>




}

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