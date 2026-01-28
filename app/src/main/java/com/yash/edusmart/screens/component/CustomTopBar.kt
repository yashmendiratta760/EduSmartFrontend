package com.yash.edusmart.screens.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yash.edusmart.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalContext

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(navController: NavHostController,
                 title: String,
                 canNavigateBack: Boolean,
                 modifier : Modifier = Modifier,
                 userType:String
)
{
    val context = LocalContext.current
    val orientation = context.resources.configuration.orientation
    TopAppBar(
        title = {

            Row (modifier = Modifier.fillMaxWidth()
                ,horizontalArrangement = Arrangement.SpaceBetween){
                Column {
                    Text(title, fontSize = 40.sp)
                    Text(
                        text = "Welcome back,$userType!", fontSize = 20.sp,
                        color = Color.Gray
                    )
                }
                Box(modifier = Modifier
                    .padding(top=15.dp,end = 10.dp)
                    .border(width = 2.dp,
                        color = Color.Blue,
                        shape = RoundedCornerShape(8.dp))
                    .background(Color.Blue.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp))
                    .width(90.dp)
                    .height(30.dp),
                    contentAlignment = Alignment.Center
                    ){
                    Text(text =
                        LocalDate.now().format(DateTimeFormatter
                            .ofPattern("EEE, MMM d", Locale.ENGLISH))
                            .toString(),
                        fontSize = 15.sp)
                }
            }
        },
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back Arrow")
                }
            }
        },
        modifier = modifier
            .then(if (orientation== Configuration.ORIENTATION_LANDSCAPE)
                Modifier.height(150.dp)
            else Modifier)
            .paint(painter = painterResource(R.drawable.student_experience_cover_2))
            .background(color = Color.Black.copy(alpha = 0.8f)),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )

    )
}
