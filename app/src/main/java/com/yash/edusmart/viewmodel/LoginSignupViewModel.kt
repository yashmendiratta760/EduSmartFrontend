package com.yash.edusmart.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yash.edusmart.login_signup.api.OtpData
import com.yash.edusmart.login_signup.api.TokenData
import com.yash.edusmart.login_signup.api.UserDTO
import com.yash.edusmart.repository.LoginSignupRepository
import com.google.gson.Gson
import com.yash.edusmart.repository.ContextRepo
import com.yash.edusmart.services.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginSignupViewModel @Inject constructor(private val loginSignupRepository: LoginSignupRepository,
    private val contextRepo: ContextRepo):ViewModel()
{


    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()



    fun showToast(message: String) {
        viewModelScope.launch {
            _events.emit(message)
        }
    }


    fun signup(userDTO: UserDTO) {
        viewModelScope.launch {
            try {
                val response = loginSignupRepository.signup(userDTO)
                val body = response.body() ?: "NOT_FOUND"
                if(response.code()==200) {
                    showToast(message = body)
                    _uiState.update {
                        it.copy(canVerify = true)
                    }
                }else{
                    showToast(message = response.errorBody()?.string() ?: "NOT_FOUND")
                }

            }catch (e: Exception){
                showToast(message = "Some Error Occurred")
                Log.d("ERROR IN SIGNUP",e.toString())
            }
        }

    }

    fun otpVerify(otpData: OtpData)
    {
        viewModelScope.launch {
            try {
                val response = loginSignupRepository.otpVerify(otpData)
                val body = response.body() ?:TokenData("NULL", "NULL",
                    "Unknown error","NULL","NULL","0","","")
                if(response.code()==200)
                {
                    Log.d("OTP_VERIFY",body.message)
                    contextRepo.saveToken(body.token)
                    contextRepo.saveUserType(body.userType)
                    contextRepo.saveEmail(body.email)
                    showToast(message = body.message)
                    _uiState.update { it->
                        it.copy(isLoggedIn = true)
                    }
                } else {
                    Log.d("OTP_VERIFY","ERROR ${response.code()}  ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                showToast(message = "Some Error Occurred")
                Log.d("ERROR IN OTP VERIFY",e.toString())
            }
        }
    }

    fun login(userDTO: UserDTO)
    {
        viewModelScope.launch {
            try {
                val response = loginSignupRepository.login(userDTO)
                val body = response.body() ?:TokenData("NULL", "NULL", "Unknown error","NULL","NULL","0","","")
                Log.d("body",body.toString())
                if(response.code()==200)
                {
                    Log.d("LOGIN",body.message)
                    contextRepo.saveToken(body.token)
                    contextRepo.saveUserType(body.userType)
                    contextRepo.saveEmail(body.email)
                    contextRepo.saveBranch(body.branch)
                    contextRepo.saveSemester(body.semester)
                    contextRepo.saveLoggedin(true)
                    contextRepo.saveName(body.name)
                    contextRepo.saveEnroll(body.enroll)

                    showToast(message = body.message)

                    _uiState.update { it->
                        it.copy(isLoggedIn = true)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorData = if(errorBody != null) Gson().fromJson(errorBody, TokenData::class.java)
                    else TokenData("NULL", "NULL", "Unknown error","NULL","NULL","0","","")
                    showToast(message = body.message)
                    Log.d("LOGIN","ERROR ${response.code()} , ${body.message} ")
                }
            } catch (e: Exception) {
                showToast(message = "Some Error Occurred")
                Log.d("ERROR IN LOGIN",e.toString())
            }
        }
    }

    fun loginWithGoogle(idToken:String)
    {
        viewModelScope.launch {
            try {
                val response = loginSignupRepository.loginWithGoogle(mapOf("idToken" to idToken))
                val body = response.body() ?:TokenData("NULL", "NULL", "Unknown error","NULL","NULL","0","","")
                if(response.code()==200)
                {
                    Log.d("LOGIN",body.message)
                    contextRepo.saveToken(body.token)
                    contextRepo.saveUserType(body.userType)
                    contextRepo.saveEmail(body.email)
                    contextRepo.saveBranch(body.branch)
                    contextRepo.saveSemester(body.semester)
                    contextRepo.saveLoggedin(true)
                    contextRepo.saveName(body.name)
                    contextRepo.saveEnroll(body.enroll)
                    showToast(message = body.message)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorData = if(errorBody != null) Gson().fromJson(errorBody, TokenData::class.java)
                    else TokenData("NULL", "NULL", "Unknown error","NULL","NULL","0","","")
                    showToast(message = body.message)
                    Log.d("LOGIN","ERROR ${response.code()} , ${body.message} ")
                }
            } catch (e: Exception) {
                showToast(message = "Some Error Occurred")
                Log.d("ERROR IN LOGIN(GOOGLE)",e.toString())
            }

        }
    }
    fun logout()
    {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggedIn = false) }
            contextRepo.clearToken()
            contextRepo.clearEmail()
            contextRepo.clearLoggedin()
            contextRepo.clearUserType()
            contextRepo.clearBranch()
            contextRepo.clearSem()
            contextRepo.clearName()
            contextRepo.clearEnroll()
        }
    }



}