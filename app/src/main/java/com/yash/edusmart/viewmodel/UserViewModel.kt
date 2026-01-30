package com.yash.edusmart.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yash.edusmart.repository.ContextRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val contextRepo: ContextRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean?> =
        contextRepo.getLoggedin()      // Flow<Boolean?>
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)


    init {
        viewModelScope.launch {
            isLoggedIn.collect { logged ->
                Log.e("IS_LOGGED_IN", logged.toString())

                if (logged == true) {
                    val email = contextRepo.getEmail().firstOrNull().orEmpty()
                    val branch = contextRepo.getBranch().firstOrNull().orEmpty()
                    val semester = contextRepo.getSemester().firstOrNull().orEmpty()
                    val name = contextRepo.getName().firstOrNull().orEmpty()
                    val enroll = contextRepo.getEnroll().firstOrNull().orEmpty()
                    val userType = contextRepo.getUserType().firstOrNull().orEmpty()

                    _uiState.update {
                        it.copy(
                            email = email,
                            branch = branch,
                            semester = semester,
                            name = name,
                            enroll = enroll,
                            userType = userType
                        )
                    }
                } else if (logged == false) {
                    _uiState.update { UserUiState() } // reset on logout (optional)
                }
                // if logged == null -> do nothing (loading)
            }
        }
    }
    fun setEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun setName(name: String) {
        _uiState.update { it.copy(name = name) }
    }
}
