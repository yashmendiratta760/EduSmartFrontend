package com.yash.edusmart.viewmodel

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yash.edusmart.api.AttendanceDTO
import com.yash.edusmart.api.StudentsListDTO
import com.yash.edusmart.data.AssignmentGetDTO
import com.yash.edusmart.data.AttendanceStatus
import com.yash.edusmart.data.AttendanceUploadDTO
import com.yash.edusmart.db.Assignments
import com.yash.edusmart.db.TimeTableDTO
import com.yash.edusmart.db.TimeTableEntries
import com.yash.edusmart.repository.AssignmentLocalRepo
import com.yash.edusmart.repository.ContextRepo
import com.yash.edusmart.repository.LocalDbRepo
import com.yash.edusmart.repository.MainAppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class MainAppViewModel @Inject constructor(
    private val mainAppRepo: MainAppRepo,
    private val localDbRepo: LocalDbRepo,
    private val assignmentLocalRepo: AssignmentLocalRepo,
    private val contextRepo: ContextRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainAppUiState())
    val uiState: StateFlow<MainAppUiState> = _uiState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
        }
    }

    // ---------------------------
    // SAFE API WRAPPER (USE THIS)
    // ---------------------------
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


    // ---------------------------
    // ASSIGNMENTS
    // ---------------------------
    fun getAssignments() {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { mainAppRepo.getAllAssignTeacher() },
                onSuccess = { list ->
                    val assignments = list ?: emptyList()
                    _uiState.update { it.copy(assignments = assignments) }
                    assignments.forEach { insertOrSync(it) }
                }
            )
        }
    }

    fun syncAssignments(latest: List<Assignments>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val latestIds = latest.map { it.id }

                // 1️⃣ Delete extra assignments
                assignmentLocalRepo.deleteExtras(latestIds)

                // 2️⃣ Insert / update current ones
                latest.forEach { assignment ->
                    assignmentLocalRepo.insert(assignment)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast(e.message ?: "Something went wrong!")
                }
            }
        }
    }


    fun markAssignment(id: Long, enroll: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { mainAppRepo.markAssignment(id, enroll) },
                onSuccess = { msg ->
                    showToast(msg ?: "Assignment Marked Successfully!")
                }
            )
        }
    }

    fun getAssignmentStudent() {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { mainAppRepo.getAllAssign() },
                onSuccess = { list ->
                    val latest = (list ?: emptyList()).map {
                        Assignments(
                            id = it.id,
                            branch = it.branch,
                            sem = it.sem,
                            enrollCom = emptyList(),
                            task = it.assignment,
                            deadline = it.deadline,
                            isCompleted = false
                        )
                    }
                    syncAssignments(latest)
                }
            )
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
                        isCompleted = false
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

    fun updateIsCompleted(id: Long, isCompleted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                assignmentLocalRepo.updateIsCompleted(id, isCompleted = isCompleted)
            } catch (e: Exception) {
                showToast(e.message ?: "Something Went Wrong!")
            }
        }
    }

    // ---------------------------
    // TIMETABLE (API + LOCAL DB)
    // ---------------------------
    fun getTimeTableByBranchAndSemester(branch: String, semester: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { mainAppRepo.getTimeTableByBranch(branch, semester.toString()) },
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

    fun getTimeTableByBranchAndSemesterTeacher(branch: String, semester: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { mainAppRepo.getTimeTableByBranchAndSemesterTeacher(branch, semester.toString()) },
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

    fun setFalseCallComplete() {
        _uiState.update { it.copy(callComplete = false) }
    }

    // ---------------------------
    // BRANCH + SUBJECTS + ATTENDANCE
    // ---------------------------
    fun getAllBranch() {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { mainAppRepo.getAllBranch() },
                onSuccess = { branches ->
                    _uiState.update { it.copy(branch = branches ?: emptyList()) }
                }
            )
        }
    }

    fun getAttendance(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { mainAppRepo.getAttendanceByEmail(email) },
                onSuccess = { attendance ->
                    _uiState.update {
                        it.copy(
                            attendance = attendance
                                ?: listOf(AttendanceDTO(LocalDate.now().toString(), "NULL", "NULL"))
                        )
                    }
                }
            )
        }
    }

    fun getAllSubjects(branch: String, semester: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { mainAppRepo.getAllSubjects(branch, semester) },
                onSuccess = { subjects ->
                    _uiState.update {
                        it.copy(
                            subjectList = subjects?.filter { it != "Break" } ?: emptyList()
                        )
                    }
                }
            )
        }
    }

    // ---------------------------
    // ATTENDANCE UPLOAD
    // ---------------------------
    @SuppressLint("ShowToast")
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
                call = { mainAppRepo.uploadAttendance(attendanceUploadDTO) },
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

    // ---------------------------
    // STUDENT LISTS
    // ---------------------------
    fun getStudentListStudentChat(branch: String, semester: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { mainAppRepo.getStudentsByBranchAndSemester(StudentsListDTO(branch, semester)) },
                onSuccess = { students ->
                    _uiState.update { it.copy(studentDataChat = students ?: emptyList()) }
                },
                onError = { err ->
                    _uiState.update { it.copy(studentDataChat = emptyList()) }
                    showToast(err)
                }
            )
        }
    }

    fun getStudentListTeacherChat(branch: String, semester: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { mainAppRepo.getStudentsByBranchAndSemesterTeacher(StudentsListDTO(branch, semester)) },
                onSuccess = { students ->
                    _uiState.update { it.copy(studentDataChat = students ?: emptyList()) }
                },
                onError = { err ->
                    _uiState.update { it.copy(studentDataChat = emptyList()) }
                    showToast(err)
                }
            )
        }
    }

    fun getStudentListTeacherAttendance(branch: String, semester: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { mainAppRepo.getStudentsByBranchAndSemesterTeacher(StudentsListDTO(branch, semester)) },
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
            safeApi(call = {mainAppRepo.getTeacherTimeTable(email)},
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

    fun deleteById(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(call = {
                mainAppRepo.deleteAssignment(id)
            },
                onSuccess = { str->
                    getAssignments()
                    showToast(str?:"Uploaded")
                },
                onError = {
                    showToast("Some error occurred")
                })
        }
    }
}
