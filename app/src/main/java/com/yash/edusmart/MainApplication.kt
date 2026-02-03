package com.yash.edusmart

import android.app.Application
import android.os.StrictMode
import com.yash.edusmart.repository.ContextRepo
import com.yash.edusmart.repository.TokenHolder
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch



@HiltAndroidApp
class MainApplication:Application()
{
    @Inject
    lateinit var contextRepo: ContextRepo
    override fun onCreate() {
        super.onCreate()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            contextRepo.getToken().filterNotNull().first()?.let { token ->
                TokenHolder.token = token
            }
        }

    }

}