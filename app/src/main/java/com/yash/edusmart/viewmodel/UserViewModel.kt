package com.yash.edusmart.viewmodel

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

    val isLoggedIn: StateFlow<Boolean> =
        contextRepo.getLoggedin()
            .map { it == true }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                false
            )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            isLoggedIn
                .filter { it }
                .collect {

                    val email = contextRepo.getEmail().filterNotNull().first()
                    val branch = contextRepo.getBranch().filterNotNull().first()
                    val semester = contextRepo.getSemester().filterNotNull().first()
                    val name = contextRepo.getName().filterNotNull().first()
                    val enroll = contextRepo.getEnroll().filterNotNull().first()
                    val userType = contextRepo.getUserType().filterNotNull().first()


                    _uiState.update {
                        it.copy(
                            email = email ?: "",
                            branch = branch ?: "",
                            semester = semester ?: "",
                            name = name ?: "",
                            enroll = enroll ?: "",
                            userType = userType ?: ""
                        )
                    }
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
