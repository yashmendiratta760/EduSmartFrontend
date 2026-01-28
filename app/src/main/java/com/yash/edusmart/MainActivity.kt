package com.yash.edusmart

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yash.edusmart.viewmodel.LoginSignupViewModel
import com.yash.edusmart.navigation.Navigation
import com.yash.edusmart.ui.theme.edusmartTheme
import com.yash.edusmart.viewmodel.ChatViewModel
import com.yash.edusmart.viewmodel.MainAppViewModel
import com.yash.edusmart.viewmodel.StudentViewModel
import com.yash.edusmart.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            edusmartTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {  innerPadding ->
                    val loginSignupViewModel : LoginSignupViewModel =  hiltViewModel()
                    val mainAppViewModel: MainAppViewModel = hiltViewModel()
                    val studentViewModel: StudentViewModel = hiltViewModel()
                    val chatViewModel: ChatViewModel = hiltViewModel()
                    val userViewModel: UserViewModel = hiltViewModel()
                    Navigation(loginSignupViewModel = loginSignupViewModel,
                        mainAppViewModel = mainAppViewModel,
                        studentViewModel = studentViewModel,
                        chatViewModel = chatViewModel,
                        userViewModel = userViewModel)
                }
            }
        }
    }
}

