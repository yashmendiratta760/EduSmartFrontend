package com.yash.edusmart.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp

@Composable
fun TasksScreen()
{
    Row(modifier = Modifier.fillMaxSize()) {
        ColouredBox(color = Color.Blue,
            imageVector = Icons.Default.Assignment,
            value = 5.toString(),
            title = "Total")
    }
}

@Composable
private fun ColouredBox(color: Color,
                        imageVector: ImageVector,
                        value: String,
                        title: String){
    Box(modifier = Modifier.background(color = color.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center){
        Column {
            Icon(imageVector = imageVector, contentDescription = title,
                tint = color)
            Text(text = value, fontSize = 10.sp)
            Text(text = title, color = Color.Gray,
                fontSize = 8.sp)
        }
    }
}