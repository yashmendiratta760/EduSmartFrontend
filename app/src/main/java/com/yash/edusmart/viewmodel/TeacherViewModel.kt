package com.yash.edusmart.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yash.edusmart.api.PresignDownloadRequest
import com.yash.edusmart.api.PresignDownloadResponse
import com.yash.edusmart.api.PresignUploadRequest
import com.yash.edusmart.api.PresignUploadResponse
import com.yash.edusmart.api.StudentsListDTO
import com.yash.edusmart.data.AssignmentGetDTO
import com.yash.edusmart.data.AttendanceStatus
import com.yash.edusmart.data.AttendanceUploadDTO
import com.yash.edusmart.db.Assignments
import com.yash.edusmart.db.TimeTableDTO
import com.yash.edusmart.db.TimeTableEntries
import com.yash.edusmart.repository.AssignmentLocalRepo
import com.yash.edusmart.repository.LocalDbRepo
import com.yash.edusmart.repository.TeacherApiRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherViewModel @Inject constructor(private val teacherApiRepo: TeacherApiRepo,
    private val localDbRepo: LocalDbRepo,
    private val assignmentLocalRepo: AssignmentLocalRepo): ViewModel()
{
    private val _uiState = MutableStateFlow(TeacherUiState())
    val uiState: StateFlow<TeacherUiState> = _uiState.asStateFlow()
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    private fun showToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
        }
    }

    private fun setLoadingTrue(){
        _uiState.update { it->
            it.copy(isLoading = true)
        }
    }
    private fun setLoadingFalse(){
        _uiState.update { it->
            it.copy(isLoading = false)
        }
    }

    private fun setChatListLoadingTrue(){
        _uiState.update { it->
            it.copy(isChatListLoading=true)
        }
    }
    private fun setChatListLoadingFalse(){
        _uiState.update { it->
            it.copy(isChatListLoading=false)
        }
    }

    private suspend fun <T> safeApi(
        call: suspend () -> retrofit2.Response<T>,
        onSuccess: suspend (T?) -> Unit,
        onError: suspend (String) -> Unit = { showToast(it) }
    ) {
        try {
            val response = call()
            if (response.isSuccessful) {
                onSuccess(response.body())
            } else {
                onError(response.errorBody()?.string() ?: "Server error (${response.code()})")
            }
        } catch (e: java.net.ConnectException) {
            onError("Can't connect to server. Check Wi-Fi/IP/Server.")
        } catch (e: java.net.SocketTimeoutException) {
            onError("Server timeout. Try again.")
        } catch (e: Exception) {
            onError(e.message ?: "Something went wrong")
        }
    }



    fun getAssignmentsTeacher(branch: String, sem: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = {
                    setLoadingTrue()
                    teacherApiRepo.getAllAssignTeacher(branch,sem) },
                onSuccess = { list ->
                    setLoadingFalse()
                    val assignments = list ?: emptyList()
                    _uiState.update { it.copy(assignments = assignments) }
                    assignments.forEach { insertOrSync(it) }
                },
                onError = {
                    setLoadingFalse()
                }
            )
        }
    }

    fun uploadAttendance(
        attendanceList: List<AttendanceStatus>,
        subjectName: String,
        time: String,
        branch: String,
        semester: Int,
        date: String
    ) {
        val attendanceUploadDTO = AttendanceUploadDTO(
            attendance = attendanceList,
            subjectName = subjectName,
            time = time,
            branch = branch,
            semester = semester,
            date = date
        )

        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { teacherApiRepo.uploadAttendance(attendanceUploadDTO) },
                onSuccess = { msg ->
                    showToast(msg ?: "Attendance uploaded")
                },
                onError = { err ->
                    Log.e("ATTENDANCE_UPLOAD", err)
                    showToast(err)
                }
            )
        }
    }

    fun getStudentListTeacherChat(branch: String, semester: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(

                call = { setChatListLoadingTrue()
                    teacherApiRepo.getStudentsByBranchAndSemesterTeacher(StudentsListDTO(branch, semester)) },
                onSuccess = { students ->
                    setChatListLoadingFalse()
                    _uiState.update { it.copy(studentDataChat = students ?: emptyList()) }
                },
                onError = { err ->
                    setChatListLoadingFalse()
                    _uiState.update { it.copy(studentDataChat = emptyList()) }
                    showToast(err)
                }
            )
        }
    }

    fun getAllBranch() {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { teacherApiRepo.getAllBranch() },
                onSuccess = { branches ->
                    _uiState.update { it.copy(branch = branches ?: emptyList()) }
                }
            )
        }
    }

    fun getTimeTableEntries(branch: String, semester: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = localDbRepo.getDataByBranchAndSemester(branch, semester)
                val entries = response.map { dto ->
                    TimeTableDTO(
                        timing = dto.timing,
                        subject = dto.subject,
                        day = dto.day
                    )
                }
                _uiState.update { it.copy(timeTableEntries = entries) }
            } catch (e: Exception) {
                showToast(e.message ?: "Something Went Wrong!")
            }
        }
    }
    fun getTimeTableByBranchAndSemesterTeacher(branch: String, semester: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { teacherApiRepo .getTimeTableByBranchAndSemesterTeacher(branch, semester.toString()) },
                onSuccess = { list ->
                    (list ?: emptyList()).forEach { it ->
                        insertOrUpdateEntry(
                            day = it.day,
                            branch = it.branch,
                            subject = it.subject,
                            timing = it.time,
                            semester = semester
                        )
                    }
                    _uiState.update { it.copy(callComplete = true) }
                }
            )
        }
    }

    fun getStudentListTeacherAttendance(branch: String, semester: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { teacherApiRepo.getStudentsByBranchAndSemesterTeacher(StudentsListDTO(branch, semester)) },
                onSuccess = { students ->
                    _uiState.update { it.copy(studentDataAttendance = students ?: emptyList()) }
                },
                onError = { err ->
                    _uiState.update { it.copy(studentDataAttendance = emptyList()) }
                    showToast(err)
                }
            )
        }
    }

    fun getTimeTableTeacher(email: String){
        viewModelScope.launch(Dispatchers.IO){
            safeApi(call = {teacherApiRepo.getTeacherTimeTable(email)},
                onSuccess = {time->
                    _uiState.update { it->
                        it.copy(timeTableTeacher = time?:emptyList())
                    }
                    Log.d("TIM",time.toString())
                },
                onError = { err ->
                    showToast(err)
                }
            )

        }
    }

    fun deleteById(id: Long,branch: String,sem: String){
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(call = {
                teacherApiRepo.deleteAssignment(id)
            },
                onSuccess = { str->
                    getAssignmentsTeacher(branch = branch, sem = sem)
                    showToast(str?:"Uploaded")
                },
                onError = {
                    showToast("Some error occurred")
                })
        }
    }


    suspend fun insertOrSync(newData: AssignmentGetDTO) {
        try {
            val match = assignmentLocalRepo.findMatching(
                branch = newData.branch,
                sem = newData.sem,
                task = newData.assignment,
                deadline = newData.deadline
            )

            if (match == null) {
                assignmentLocalRepo.insert(
                    Assignments(
                        id = newData.id,
                        branch = newData.branch,
                        sem = newData.sem,
                        enrollCom = newData.enroll,
                        task = newData.assignment,
                        deadline = newData.deadline,
                        isCompleted = false,
                        path = newData.path
                    )
                )
            } else {
                if (match.id != newData.id) {
                    assignmentLocalRepo.updateIdAndEnroll(
                        oldId = match.id,
                        newId = newData.id,
                        newEnrollCom = newData.enroll
                    )
                } else {
                    assignmentLocalRepo.updateEnrollOnly(
                        id = match.id,
                        newEnrollCom = newData.enroll
                    )
                }
            }
        } catch (e: Exception) {
            // Avoid toast spam inside loops if you want:
            // Log.e("insertOrSync", "Failed", e)
            showToast(e.message ?: "Something Went Wrong!")
        }
    }

    suspend fun presignUploadSuspend(request: PresignUploadRequest): PresignUploadResponse {
        Log.d("HIT","JIT")
        val res = teacherApiRepo.preSignUpload(request)
        Log.d("PRESIGN", "code=${res.code()} ok=${res.isSuccessful} body=${res.body()} err=${res.errorBody()?.string()}")


        if (!res.isSuccessful) {
            val err = res.errorBody()?.string()
            throw RuntimeException("presignUpload failed: ${res.code()} ${res.message()} body=$err")
        }

        return res.body()
            ?: throw RuntimeException("presignUpload success but body is null (converter/model mismatch?)")
    }

    suspend fun preResponseDownload(response: PresignDownloadRequest): PresignDownloadResponse{

        val res = teacherApiRepo.preSignDownload(response)
        if (!res.isSuccessful) {
            val err = res.errorBody()?.string()
            throw RuntimeException("presignUpload failed: ${res.code()} ${res.message()} body=$err")
        }

        return res.body()
            ?: throw RuntimeException("presignUpload success but body is null (converter/model mismatch?)")
    }





    fun insertOrUpdateEntry(
        day: String,
        branch: String,
        subject: String,
        timing: String,
        semester: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existingEntry = localDbRepo.getDataByBranchAndSemester(branch, semester)
                    .firstOrNull { it.day == day && it.timing == timing }

                if (existingEntry != null) {
                    if (existingEntry.subject != subject || existingEntry.timing != timing || existingEntry.day != day) {
                        val updatedEntry = TimeTableEntries(
                            id = existingEntry.id,
                            day = day,
                            branch = branch,
                            subject = subject,
                            timing = timing,
                            semester = semester
                        )
                        localDbRepo.update(updatedEntry)
                    }
                } else {
                    val newEntry = TimeTableEntries(
                        day = day,
                        branch = branch,
                        subject = subject,
                        timing = timing,
                        semester = semester
                    )
                    localDbRepo.insert(newEntry)
                }
            } catch (e: Exception) {
                showToast(e.message ?: "Something Went Wrong!")
            }
        }
    }
}