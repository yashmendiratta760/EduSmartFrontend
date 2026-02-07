package com.yash.edusmart.repository

import com.yash.edusmart.api.ChatEntity
import com.yash.edusmart.api.PresignDownloadRequest
import com.yash.edusmart.api.PresignDownloadResponse
import com.yash.edusmart.api.PresignUploadRequest
import com.yash.edusmart.api.PresignUploadResponse
import com.yash.edusmart.api.StudentData
import com.yash.edusmart.api.StudentsListDTO
import com.yash.edusmart.api.TeacherApi
import com.yash.edusmart.data.AssignmentGetDTO
import com.yash.edusmart.data.AttendanceUploadDTO
import com.yash.edusmart.data.TimeTableEntry
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject
import javax.inject.Singleton

interface TeacherApiRepo {
    suspend fun uploadAttendance(attendanceUploadDTO: AttendanceUploadDTO): Response<String>

    suspend fun getStudentsByBranchAndSemesterTeacher(studentsListDTO : StudentsListDTO): Response<List<StudentData>>

    suspend fun getAllBranch(): Response<List<String>>

    suspend fun getTimeTableByBranchAndSemesterTeacher(branch: String,semester: String): Response<List<TimeTableEntry>>

    suspend fun getAllAssignTeacher(branch: String,sem: String): Response<List<AssignmentGetDTO>>

    suspend fun getTeacherTimeTable(): Response<List<TimeTableEntry>>

    suspend fun deleteAssignment(id: Long): Response<String>

    suspend fun getTeacherGroupMessages(branch: String, sem: String): Response<List<ChatEntity>>

    suspend fun getTeacherPrivateConversation(email: String, receiverEmail: String): Response<List<ChatEntity>>

    suspend fun preSignUpload(
        request: PresignUploadRequest
    ): Response<PresignUploadResponse>

    @POST("/teacher/presign-download")
    suspend fun preSignDownload(
       request: PresignDownloadRequest
    ): Response<PresignDownloadResponse>
}
class TeacherApiRepoImpl @Inject constructor(private val teacherApi: TeacherApi): TeacherApiRepo{
    override suspend fun uploadAttendance(attendanceUploadDTO: AttendanceUploadDTO): Response<String> {
        return teacherApi.uploadAttendance(attendanceUploadDTO)
    }

    override suspend fun getStudentsByBranchAndSemesterTeacher(studentsListDTO: StudentsListDTO): Response<List<StudentData>> {
        return teacherApi.getStudentsByBranchAndSemesterTeacher(studentsListDTO)
    }

    override suspend fun getAllBranch(): Response<List<String>> {
        return teacherApi.getAllBranch()
    }

    override suspend fun getTimeTableByBranchAndSemesterTeacher(
        branch: String,
        semester: String
    ): Response<List<TimeTableEntry>> {
        return teacherApi.getTimeTableByBranchAndSemesterTeacher(branch,semester)
    }

    override suspend fun getAllAssignTeacher(
        branch: String,
        sem: String
    ): Response<List<AssignmentGetDTO>> {
        return teacherApi.getAllAssignTeacher(branch,sem)
    }

    override suspend fun getTeacherTimeTable(): Response<List<TimeTableEntry>> {
        return teacherApi.getTeacherTimeTable()
    }

    override suspend fun deleteAssignment(id: Long): Response<String> {
        return teacherApi.deleteAssignment(id)
    }

    override suspend fun getTeacherGroupMessages(
        branch: String,
        sem: String
    ): Response<List<ChatEntity>> {
        return teacherApi.getGroupMessagesTeacher(branch,sem)
    }

    override suspend fun getTeacherPrivateConversation(
        email: String,
        receiverEmail: String
    ): Response<List<ChatEntity>> {
        return teacherApi.getPrivateConversationTeacher(email,receiverEmail)
    }

    override suspend fun preSignUpload(request: PresignUploadRequest): Response<PresignUploadResponse> {
        return teacherApi.preSignUpload(request)
    }

    override suspend fun preSignDownload(request: PresignDownloadRequest): Response<PresignDownloadResponse> {
        return teacherApi.preSignDownload(request)
    }

}
@Module
@InstallIn(SingletonComponent::class)
abstract class TeacherAppRepoModule{
    @Binds
    @Singleton
    abstract fun joinTeacherRepoImpl(impl: TeacherApiRepoImpl): TeacherApiRepo
}