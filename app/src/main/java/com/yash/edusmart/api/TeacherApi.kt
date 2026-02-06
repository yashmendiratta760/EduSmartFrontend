package com.yash.edusmart.api

import com.yash.edusmart.data.AssignmentGetDTO
import com.yash.edusmart.data.AttendanceUploadDTO
import com.yash.edusmart.data.TimeTableEntry
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface TeacherApi{

    @GET("/teacher/getTimeTableByDay")
    suspend fun getTimeTableByBranchAndSemesterTeacher(
        @Query("branch") branch: String,
        @Query("semester") semester: String
    ): Response<List<TimeTableEntry>>

    @POST("/teacher/uploadAttendance")
    suspend fun uploadAttendance(
        @Body attendanceUploadDTO: AttendanceUploadDTO
    ): Response<String>


    @POST("/teacher/getStudentsListTeacher")
    suspend fun getStudentsByBranchAndSemesterTeacher(
        @Body studentsListDTO : StudentsListDTO
    ): Response<List<StudentData>>

    @GET("/teacher/getAllBranch")
    suspend fun getAllBranch(): Response<List<String>>

    @GET("/teacher/getAllAssignmentsByBranchAndSem")
    suspend fun getAllAssignTeacher(
        @Query("branch") branch: String,
        @Query("sem") sem: String
    ): Response<List<AssignmentGetDTO>>

    @GET("/teacher/getMyTImeTable")
    suspend fun getTeacherTimeTable(
        @Query("email") email : String
    ): Response<List<TimeTableEntry>>

    @PUT("/teacher/deleteAssignment")
    suspend fun deleteAssignment(
        @Query("id") id: Long
    ): Response<String>

    @GET("/teacher/getMessagesByBranchAndSem")
    suspend fun getGroupMessagesTeacher(
        @Query("branch") branch: String,
        @Query("sem") sem: String
    ): Response<List<ChatEntity>>

    @GET("/teacher/getPvtMsg")
    suspend fun getPrivateConversationTeacher(
        @Query("email") email: String,
        @Query("receiverEmail") receiverEmail: String
    ): Response<List<ChatEntity>>

    @POST("/teacher/presign-upload")
    suspend fun preSignUpload(
        @Body request: PresignUploadRequest
    ): Response<PresignUploadResponse>

    @POST("/teacher/presign-download")
    suspend fun preSignDownload(
        @Body req: PresignDownloadRequest
    ): Response<PresignDownloadResponse>

}

data class PresignUploadRequest(
    val fileName: String
)

data class PresignUploadResponse(
    val path: String,
    val uploadUrl: String
)

data class PresignDownloadRequest (
    val path: String
)
data class PresignDownloadResponse (
    val path: String
)