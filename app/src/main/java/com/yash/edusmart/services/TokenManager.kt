package com.yash.edusmart.services

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object TokenManager {
    private val Context.dataStore by preferencesDataStore("user_token")
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")

    private val USER_TYPE = stringPreferencesKey("user_type")

    private val EMAIL = stringPreferencesKey("email")

    private val BRANCH = stringPreferencesKey("branch")
    private val SEMESTER = stringPreferencesKey("semester")
    private val IS_LOGGEDIN = booleanPreferencesKey("login")

    private val NAME = stringPreferencesKey("name")

    private val ENROLL_NO = stringPreferencesKey("enroll")

    suspend fun saveEnroll(context: Context, enroll: String) {
        context.dataStore.edit { prefs ->
            prefs[ENROLL_NO] = enroll
        }
    }

    fun getEnroll(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[ENROLL_NO]
        }
    }

    suspend fun clearEnroll(context: Context) {
        context.dataStore.edit { prefs ->
                prefs.remove(ENROLL_NO)
        }
    }

    suspend fun saveName(context: Context, name: String) {
        context.dataStore.edit { prefs ->
            prefs[NAME] = name
        }
    }

    fun getName(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[NAME]
        }
    }

    suspend fun clearName(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(NAME)
        }
    }


    suspend fun saveLoggedin(context: Context, isLogin: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGEDIN] = isLogin
        }
    }

    fun getLoggedin(context: Context): Flow<Boolean?> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_LOGGEDIN]
        }
    }

    suspend fun clearLogin(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(IS_LOGGEDIN)
        }
    }

    suspend fun saveBranch(context: Context, branch: String) {
        context.dataStore.edit { prefs ->
            prefs[BRANCH] = branch
        }
    }

    fun getBranch(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[BRANCH]
        }
    }

    suspend fun clearBranch(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(BRANCH)
        }
    }
    suspend fun saveSemester(context: Context, semester: String) {
        context.dataStore.edit { prefs ->
            prefs[SEMESTER] = semester
        }
    }

    fun getSemester(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[SEMESTER]
        }
    }

    suspend fun clearSemester(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(SEMESTER)
        }
    }
    suspend fun saveEmail(context: Context, email: String) {
        context.dataStore.edit { prefs ->
            prefs[EMAIL] = email
        }
    }

    fun getEmail(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[EMAIL]
        }
    }

    suspend fun clearEmail(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(EMAIL)
        }
    }

    suspend fun saveToken(context: Context, token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    suspend fun saveUserType(context: Context,userType: String){
        context.dataStore.edit { prefs->
            prefs[USER_TYPE] = userType
        }
    }

    fun getUserType(context: Context): Flow<String?>{
        return context.dataStore.data.map { pref->
            pref[USER_TYPE]
        }
    }

    fun getToken(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    suspend fun clearToken(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }
    suspend fun clearUserType(context: Context){
        context.dataStore.edit { prefs->
            prefs.remove(USER_TYPE)
        }
    }
}
