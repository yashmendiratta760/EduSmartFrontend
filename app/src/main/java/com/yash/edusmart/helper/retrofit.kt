package com.yash.edusmart.helper

import android.content.Context
import com.yash.edusmart.api.StudentApi
import com.yash.edusmart.api.TeacherApi
import com.yash.edusmart.db.RoomDb
import com.yash.edusmart.api.LoginSignupApi
import com.yash.edusmart.services.isEmulator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenHolder.token

        if (token.isNullOrBlank() || !token.contains(".")) {
            return chain.proceed(chain.request())
        }

        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }
}



fun getUrl(): String {
    return if (isEmulator()) {
        "http://10.0.2.2:8080/"
    } else {
        "http://192.168.0.103:8080/"

    }
}


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient):Retrofit
    {
        return Retrofit.Builder()
            .baseUrl("https://edusmartbackend-z95q.onrender.com/")
//            .baseUrl(getUrl())
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideLoginSignupApi(retrofit: Retrofit):LoginSignupApi
    {
        return retrofit.create(LoginSignupApi::class.java)
    }



    @Provides
    @Singleton
    fun providesTeacherApi(retrofit: Retrofit): TeacherApi
    {
        return retrofit.create(TeacherApi::class.java)
    }

    @Provides
    @Singleton
    fun providesStudentApi(retrofit: Retrofit): StudentApi
    {
        return retrofit.create(StudentApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRoomDb(@ApplicationContext context: Context): RoomDb
    {
        return RoomDb.getDatabase(context)
    }

    @Provides
    fun provideTimeTableDao(db: RoomDb) = db.timeTableDao()

    @Provides
    fun provideChatDao(db: RoomDb) = db.chatDao()

    @Provides
    fun provideAssDao(db: RoomDb) = db.assignmentDao()

    @Provides
    fun provideHolidayDao(db: RoomDb)=db.HolidaysDao()
}