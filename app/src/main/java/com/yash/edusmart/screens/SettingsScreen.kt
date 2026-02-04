package com.yash.edusmart.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yash.edusmart.R
import com.yash.edusmart.navigation.Screens
import com.yash.edusmart.screens.component.student.SettingsOptionBox
import com.yash.edusmart.viewmodel.LoginSignupViewModel
import com.yash.edusmart.viewmodel.UserUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Penta<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth : E
)
@Composable
fun SettingsScreen(innerPadding: PaddingValues,
                   onEditProfileClick:()-> Unit,
                   loginSignupViewModel: LoginSignupViewModel,
                   navController: NavHostController,
                   userUiState: UserUiState){




    val coroutineScope = rememberCoroutineScope()


    val accountData:List<Penta<String, String, ImageVector, Color, () -> Unit>>  = listOf(
        Penta("Edit Profile","Update your personal information", Icons.Default.Person,Color.Blue) {},
        Penta("Change Password","Update your password", Icons.Default.Lock,Color.Cyan) {},
        Penta("Email Settings","Manage email preferences", Icons.Default.Email,Color.Green) {}
    )
    val notificationsData :List<Penta<String, String, ImageVector, Color, () -> Unit>> = listOf(
        Penta("Push Notifications","Enable push notifications",Icons.Default.Notifications,Color(0xFFE36D24)) {},
        Penta("Email Notifications","Receive updates via email", Icons.Default.Email,Color(0xFFB514F0)) {},
        Penta("Class Reminders","Get class reminders one day before",Icons.Default.CalendarToday,Color.Blue) {},
        Penta("Attendance Alerts","Low attendance notifications", Icons.Default.NotificationsActive,Color.Red) {}
    )
    val supportData:List<Penta<String, String, ImageVector, Color, () -> Unit>>  = listOf(
        Penta("Logout","", Icons.Outlined.Delete,Color.Red) {
            coroutineScope.launch(Dispatchers.IO) {
                loginSignupViewModel.logout()
                withContext(Dispatchers.Main) {
                    navController.navigate(Screens.Login.name) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            }
        },
        Penta("Help & Support","Get help and FAQ's", Icons.Default.QuestionMark,Color.Magenta) {},
        Penta("About EduSmart","Version 1.0.0",Icons.Default.BookmarkBorder,Color.Gray) {}
    )

    LazyColumn(modifier = Modifier
        .padding(innerPadding)){
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0B042A),
                                Color(0xFF13042F)
                            )
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = 1.dp, color = Color(0xFF210D5E),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable(
                        onClick = {
                            onEditProfileClick()
                        }
                    )
            ) {
                Row(modifier = Modifier.padding(10.dp)) {
                    Box {

                        Image(
                            painter = painterResource(R.drawable.ic_launcher_background),
                            contentDescription = "PFP",
                            modifier = Modifier
                                .size(85.dp)
                                .clip(shape = RoundedCornerShape(80.dp))
                        )
                        Box(Modifier.align(Alignment.BottomEnd)
                            .size(35.dp)
                            .clickable(
                                onClick = {
//                                    launcher.launch("image/*")
                                }
                            )
                            .background(color = Color.Blue.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(30.dp)),
                            contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.Edit,
                                contentDescription = "Edit icon"
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.padding(
                            top = 10.dp,
                            start = 10.dp
                        )
                    ) {
                        if(userUiState.userType == "STUDENT") {
                            Text(
                                text = userUiState.enroll,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "Name: " + userUiState.name,
                            color = Color.White
                        )
                        if(userUiState.userType == "STUDENT") {
                            Text(
                                text = "Branch: " + userUiState.branch + "   Sem: " + userUiState.semester,
                                color = Color.White
                            )
                        }
                        Text(
                            text = userUiState.email,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 5.dp)
                        )

                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "Account", color = Color.LightGray,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 10.dp,
                    top = 10.dp,
                    bottom = 10.dp))
            SettingsOptionBox(accountData)

            Text(text = "Notifications", color = Color.LightGray,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 10.dp,
                    top = 10.dp,
                    bottom = 10.dp))
            SettingsOptionBox(notificationsData)

            Text(text = "Support", color = Color.LightGray,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 10.dp,
                    top = 10.dp,
                    bottom = 10.dp))
            SettingsOptionBox(supportData)
        }
    }
}

