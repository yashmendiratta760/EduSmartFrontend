package com.yash.edusmart.repository

import com.yash.edusmart.api.LoginSignupApi
import com.yash.edusmart.api.OtpData
import com.yash.edusmart.api.TokenData
import com.yash.edusmart.api.UserDTO
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface LoginSignupRepository
{
    suspend fun signup(userDTO: UserDTO):Response<String>
    suspend fun otpVerify(otpData: OtpData):Response<TokenData>
    suspend fun login(userDTO: UserDTO):Response<TokenData>
    suspend fun loginWithGoogle(body:Map<String,String>):Response<TokenData>
}

class LoginSignupRepoImpl @Inject constructor(private val loginSignupApi: LoginSignupApi):LoginSignupRepository
{
    override suspend fun signup(userDTO: UserDTO): Response<String> {
        return loginSignupApi.signup(userDTO)
    }

    override suspend fun otpVerify(otpData: OtpData): Response<TokenData> {
        return loginSignupApi.verifyOtp(otpData)
    }

    override suspend fun login(userDTO: UserDTO): Response<TokenData> {
        return loginSignupApi.login(userDTO)
    }

    override suspend fun loginWithGoogle(body: Map<String, String>): Response<TokenData> {
        return loginSignupApi.loginWithGoogle(body)
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule
{
    @Binds
    @Singleton
    abstract fun linkLoginSignupRepo(impl:LoginSignupRepoImpl):LoginSignupRepository
}

