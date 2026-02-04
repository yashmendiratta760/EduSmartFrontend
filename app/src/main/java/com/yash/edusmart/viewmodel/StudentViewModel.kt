package com.yash.edusmart.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yash.edusmart.api.AttendanceDTO
import com.yash.edusmart.api.StudentsListDTO
import com.yash.edusmart.db.Assignments
import com.yash.edusmart.db.TimeTableDTO
import com.yash.edusmart.db.TimeTableEntries
import com.yash.edusmart.repository.AssignmentLocalRepo
import com.yash.edusmart.repository.ContextRepo
import com.yash.edusmart.repository.LocalDbRepo
import com.yash.edusmart.repository.StudentApiRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.collections.forEach

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class StudentViewModel @Inject constructor(private val contextRepo: ContextRepo,
    private val studentApiRepo: StudentApiRepo,
    private val assignmentLocalRepo: AssignmentLocalRepo,
    private val localDbRepo: LocalDbRepo) : ViewModel() {


    private val _uiState = MutableStateFlow(StudentUiState())
    val studentUiState: StateFlow<StudentUiState> = _uiState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean?> =
        contextRepo.getLoggedin()
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

//
//    init {
//        viewModelScope.launch {
//            isLoggedIn.collect { logged ->
//                Log.e("IS_LOGGED_IN", logged.toString())
//
//                if (logged == true) {
//                    val branch = contextRepo.getBranch().firstOrNull()
//                    val semester = contextRepo.getSemester().firstOrNull()
//
//                    if (branch != null && semester != null) {
//                        _studentUiState.update {
//                            it.copy(
//                                branch = branch,
//                                semester = semester.toIntOrNull() ?: -1
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
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



    fun getHolidaysServer(){
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = {studentApiRepo.getHolidays()},
                onSuccess = {h->
                    _uiState.update { it->
                        it.copy(holidays = h?:emptyList())
                    }
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
                call = { studentApiRepo.markAssignment(id, enroll) },
                onSuccess = { msg ->
                    showToast(msg ?: "Assignment Marked Successfully!")
                }
            )
        }
    }

    fun getAllTeacher(branch: String,sem: String){
        viewModelScope.launch(Dispatchers.IO){
            safeApi(call = {
                setChatListLoadingTrue()
                studentApiRepo.getAllTeacher(branch,sem)},
                onSuccess = {tea->
                    setChatListLoadingFalse()
                    _uiState.update { it->
                        it.copy(teacher = tea?:emptyList())
                    }
                }, onError = {err->
                    setChatListLoadingFalse()
                }

            )
        }
    }

    fun getAssignmentStudent(branch: String,sem: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = {
                    setLoadingTrue()
                    studentApiRepo.getAllAssign(branch,sem) },
                onSuccess = { list ->
                    setLoadingFalse()
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
                },
                onError = {
                    setLoadingFalse()
                }
            )
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
                call = { studentApiRepo.getTimeTableByBranch(branch, semester.toString()) },
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


    fun getAttendance(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = {
                    setLoadingTrue()
                    studentApiRepo.getAttendanceByEmail(email)
                },
                onSuccess = { attendance ->
                    setLoadingFalse()
                    _uiState.update {
                        it.copy(
                            attendance = attendance
                                ?: listOf(AttendanceDTO(LocalDate.now().toString(), "NULL", "NULL"))
                        )
                    }
                },
                onError = {
                    setLoadingFalse()
                }
            )
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

    fun getAllSubjects(branch: String, semester: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = { studentApiRepo.getAllSubjects(branch, semester) },
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



    fun getStudentListStudentChat(branch: String, semester: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = {
                    setChatListLoadingTrue()
                    studentApiRepo.getStudentsByBranchAndSemester(StudentsListDTO(branch, semester)) },
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

}