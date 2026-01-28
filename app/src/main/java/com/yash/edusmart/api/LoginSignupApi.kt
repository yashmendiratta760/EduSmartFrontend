package com.yash.edusmart.login_signup.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginSignupApi
{
    @POST("users/send-otp")
    suspend fun signup(
        @Body userDTO: UserDTO
    ):Response<String>

    @POST("users/signup")
    suspend fun verifyOtp(
        @Body otpData: OtpData
    ):Response<TokenData>

    @POST("users/login")
    suspend fun login(
        @Body userDTO: UserDTO
    ):Response<TokenData>

    @POST("users/google")
    suspend fun loginWithGoogle(
        @Body body : Map<String,String>
    ):Response<TokenData>


}

data class UserDTO(
    val email:String,
    val password:String,
    val userType:String?=null
)
data class OtpData (
    val otp:String,
    val email:String
)
data class TokenData(
    val email: String,
    val token:String,
    val message:String,
    val userType: String,
    val branch: String,
    val semester: String,
    val name: String,
    val enroll: String
)
