package com.yash.edusmart.screens.component.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AttendanceAlert(attendance: Float,
                    onClick:()-> Unit)
{
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
        .padding(20.dp)
        .background(
            brush = Brush.linearGradient(colors = listOf(Color(color = 0xFF0F2E55).copy(alpha = 0.5f),
                Color(0xFF1F4B80)
            )),
            shape = RoundedCornerShape(30.dp)
        )
        .clickable(onClick = {
            onClick()
        })){
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)) {
            Text(text = "Attendance", fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp)
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Text(text = "Your overall attendance is ${attendance}%.",
                    fontSize = 20.sp)
            }
        }
    }
}