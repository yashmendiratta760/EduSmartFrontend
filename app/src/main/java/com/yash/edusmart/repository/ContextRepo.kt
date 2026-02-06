package com.yash.edusmart.repository

import android.content.Context
import com.yash.edusmart.helper.TokenHolder
import com.yash.edusmart.services.TokenManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContextRepo @Inject constructor(
    @ApplicationContext private val context: Context
){

    suspend fun saveBranch(branch: String) {
        TokenManager.saveBranch(context, branch)
    }

    fun getBranch(): Flow<String?> {
        return TokenManager.getBranch(context)
    }
    suspend fun saveSemester(semester: String) {
        TokenManager.saveSemester(context, semester)
    }

    fun getSemester(): Flow<String?> {
        return TokenManager.getSemester(context)
    }
    suspend fun saveToken(token: String) {
        TokenManager.saveToken(context, token)
        TokenHolder.token = token
    }

    suspend fun saveEmail(email: String) {
        TokenManager.saveEmail(context, email)
    }

    suspend fun saveUserType(userType: String) {
        TokenManager.saveUserType(context, userType)
    }
    fun getUserType(): Flow<String?> {

        return TokenManager.getUserType(context)
    }

     fun getToken(): Flow<String?> {
        return TokenManager.getToken(context)
    }
     fun getEmail(): Flow<String?> {
        return TokenManager.getEmail(context)
    }

    suspend fun clearToken(){
        TokenManager.clearToken(context)
    }

    suspend fun clearBranch(){
        TokenManager.clearBranch(context)
    }
    suspend fun clearSem(){
        TokenManager.clearSemester(context)
    }

    suspend fun clearUserType(){
        TokenManager.clearUserType(context)
    }
    suspend fun clearEmail(){
        TokenManager.clearEmail(context)
    }

    suspend fun saveLoggedin(isLogin: Boolean) {
        TokenManager.saveLoggedin(context, isLogin)
    }

    fun getLoggedin(): Flow<Boolean?> {
        return TokenManager.getLoggedin(context)
    }

    suspend fun clearLoggedin(){
        TokenManager.clearLogin(context)
    }

    suspend fun saveEnroll(enroll: String) {
        TokenManager.saveEnroll(context,enroll)
    }

    fun getEnroll(): Flow<String?> {
        return TokenManager.getEnroll(context)
    }

    suspend fun clearEnroll() {
        TokenManager.clearEnroll(context)
    }

    suspend fun saveName(name: String) {
        TokenManager.saveName(context,name)
    }

    fun getName(): Flow<String?> {
        return TokenManager.getName(context)
    }

    suspend fun clearName() {
        TokenManager.clearName(context)
    }




}
