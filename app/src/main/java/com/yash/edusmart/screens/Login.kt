package com.yash.edusmart.login_signup.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.yash.edusmart.R
import com.yash.edusmart.login_signup.api.UserDTO
import com.yash.edusmart.login_signup.screens.component.CustomTextField
import com.yash.edusmart.login_signup.screens.component.LoginSignupBackground
import com.yash.edusmart.viewmodel.LoginSignupViewModel
import com.yash.edusmart.navigation.Screens
import com.yash.edusmart.services.TokenManager
import com.yash.edusmart.viewmodel.LoginUiState
import kotlinx.coroutines.launch

@Composable
fun Login(loginSignupViewModel: LoginSignupViewModel,
           navController: NavHostController,
          isDarkTheme:Boolean,
          loginUiState: LoginUiState
)
{
    val context = LocalContext.current
    val corountineScope = rememberCoroutineScope()

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestIdToken("150979112308-q20c7m6q2dcna7f4lhjv9ukkss158114.apps.googleusercontent.com")
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)


    LaunchedEffect(loginUiState.isLoggedIn) {
        if (loginUiState.isLoggedIn){
            TokenManager.getUserType(context).collect{user ->

                navController.navigate(Screens.Loading.name ) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loginSignupViewModel.events.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }




    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task: Task<GoogleSignInAccount> =
            GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken

            if (idToken != null) {
                // ✅ Now send this token to your backend
                corountineScope.launch {
                    loginSignupViewModel.loginWithGoogle(idToken)

                }
                Log.d("tokenGoogle",idToken)

                Toast.makeText(context, "Google Sign-in successful!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Log.d("GOOGLE ERROR",e.toString())
            Toast.makeText(context, "Google sign-in failed", Toast.LENGTH_SHORT).show()
        }
    }



    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    Box(Modifier.fillMaxSize()) {
        LoginSignupBackground(isDarkTheme)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                Image(painter = painterResource(R.drawable.resource_management_2),
                    contentDescription = null,
                    modifier = Modifier.size(350.dp))
            }
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Email"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                focusedContainerColor = if(!isDarkTheme) Color(0xFF292C31)
                else Color(0xFF515152),
                unfocusedContainerColor = if(!isDarkTheme) Color(0xFF292C31)
                else Color(0xFF515152)
            )
            Spacer(modifier = Modifier.padding(6.dp))
            CustomTextField(
                value = pass,
                onValueChange = { pass = it },
                label = "Password",
                leadingIcon = {
                    Icon(
                        Icons.Default.Password,
                        contentDescription = "Password"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                focusedContainerColor = if(!isDarkTheme) Color(0xFF292C31)
                else Color(0xFF515152),
                unfocusedContainerColor = if(!isDarkTheme) Color(0xFF292C31)
                else Color(0xFF515152)
            )
            Spacer(modifier = Modifier.padding(6.dp))

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A68DA)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp), onClick = {
                    loginSignupViewModel.login(UserDTO(email, pass))



                }) {
                Text(
                    text = "Login",
                    textAlign = TextAlign.Center

                )

            }
            Row(modifier = Modifier.padding(top = 4.dp)) {
                Text(text = "Not a user? ",
                    color = if(isDarkTheme) Color.White else Color.Black

                )
                Text(
                    text = "Signup",
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = {
                        navController.navigate(Screens.Signup.name){
                            popUpTo(0){
                                inclusive=true
                            }
                        }
                    })
                )
            }

            Text(
                text = "or Sign in with",
                modifier = Modifier.padding(4.dp),
                color = if(isDarkTheme) Color.White else Color.Black            )
            Image(
                painter = painterResource(R.drawable.google_logo),
                contentDescription = "Google logo",
                modifier = Modifier
                    .clickable(onClick = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            val signInIntent = googleSignInClient.signInIntent
                            googleSignInLauncher.launch(signInIntent)
                        }
                    })
                    .size(50.dp)
            )
        }
    }
}