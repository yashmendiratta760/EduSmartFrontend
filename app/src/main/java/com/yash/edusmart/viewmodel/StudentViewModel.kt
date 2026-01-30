package com.yash.edusmart.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yash.edusmart.repository.ContextRepo
import com.yash.edusmart.repository.HolidayRepo
import com.yash.edusmart.repository.MainAppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class StudentViewModel @Inject constructor(private val contextRepo: ContextRepo,
    private val mainAppRepo: MainAppRepo) : ViewModel() {


    private val _studentUiState = MutableStateFlow(StudentUiState())
    val studentUiState: StateFlow<StudentUiState> = _studentUiState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean?> =
        contextRepo.getLoggedin()      // Flow<Boolean?>
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)


    init {
        viewModelScope.launch {
            isLoggedIn.collect { logged ->
                Log.e("IS_LOGGED_IN", logged.toString())

                if (logged == true) {
                    val branch = contextRepo.getBranch().firstOrNull()
                    val semester = contextRepo.getSemester().firstOrNull()

                    if (branch != null && semester != null) {
                        _studentUiState.update {
                            it.copy(
                                branch = branch,
                                semester = semester.toIntOrNull() ?: -1
                            )
                        }
                    }
                }
            }
        }
    }
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




    fun setDay(day: String){
        _studentUiState.update { it->
            it.copy(daySelected = day)
        }
    }

    fun setSubject(subject: String)
    {
        _studentUiState.update { it->
            it.copy(selectedSubject = subject)
        }
    }

    fun setScreen(screenIndex: Int){
        _studentUiState.update { it->
            it.copy(screenOpened = screenIndex)
        }
    }

    fun setMonth(month: LocalDate){
        _studentUiState.update { it->
            it.copy(selectedMonth = month)
        }
    }

    fun getHolidaysServer(){
        viewModelScope.launch(Dispatchers.IO) {
            safeApi(
                call = {mainAppRepo.getHolidays()},
                onSuccess = {h->
                    _studentUiState.update { it->
                        it.copy(holidays = h?:emptyList())
                    }
                }
            )

        }
    }

}