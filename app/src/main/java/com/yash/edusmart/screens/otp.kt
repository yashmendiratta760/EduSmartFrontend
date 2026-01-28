package com.yash.edusmart.login_signup.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yash.edusmart.viewmodel.LoginSignupViewModel
import kotlinx.coroutines.launch
import com.yash.edusmart.R
import com.yash.edusmart.login_signup.api.OtpData
import com.yash.edusmart.login_signup.screens.component.LoginSignupBackground
import com.yash.edusmart.login_signup.screens.component.OtpInput
import com.yash.edusmart.navigation.Screens
import com.yash.edusmart.viewmodel.LoginUiState

@Composable
fun OtpVerify(loginSignupViewModel: LoginSignupViewModel,
        navController: NavHostController,
              email:String,
              isDarkTheme:Boolean,
              loginUiState: LoginUiState) {
    
    val context = LocalContext.current

    var otpe by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(loginUiState.isLoggedIn) {
        if (loginUiState.isLoggedIn){
            navController.navigate(Screens.Loading.name){
                popUpTo(0) {
                    inclusive=true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loginSignupViewModel.events.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        LoginSignupBackground(isDarkTheme)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 16.dp, end = 16.dp, bottom = 150.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.resource_management_2),
                    contentDescription = "Verification Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(200.dp)
//                        .padding(96.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Verification",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Otp has been sent to your email.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)

            )
            Spacer(modifier = Modifier.height(50.dp))

            Spacer(modifier = Modifier.height(10.dp))

            OtpInput(isDarkTheme) { otp ->
                otpe = otp
            }


            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        loginSignupViewModel.otpVerify(otpData = OtpData(otpe, email = email))
                        Log.d("EMAIL",email)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(text = "Verify")
            }



        }
    }
}

