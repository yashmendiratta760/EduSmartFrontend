package com.yash.edusmart.screens.component.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yash.edusmart.screens.Penta

@Composable
fun SettingsOptionBox(data: List<Penta<String, String, ImageVector, Color,()-> Unit>>){
    Column(modifier = Modifier
        .padding(5.dp)
        .border(width = (0.5).dp,
            color = Color.Gray,
            shape = RoundedCornerShape(10.dp))) {
        data.forEachIndexed { index,it ->
            Column(modifier = Modifier.padding(top = 10.dp)
                .fillMaxWidth()
                .clickable(
                    onClick = {
                    it.fifth()
                    }
                )) {
                Row(modifier = Modifier.padding(start = 6.dp,
                    top=5.dp)){
                    Box(
                        modifier = Modifier.size(40.dp)
                            .background(
                                color = it.fourth.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            it.third, contentDescription = it.first,
                            tint = it.fourth
                        )
                    }
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        val paddingLogout = if(it.first=="Logout") 6.5.dp else 0.dp
                        Text(
                            text = it.first,
                            color = Color.White,
                            fontSize = if(it.first!="Logout") 15.sp else 20.sp,
                            modifier = Modifier.padding(top = paddingLogout)
                        )
                        if(it.first!="Logout") {
                            Text(
                                text = it.second,
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                        }

                    }

                }
                if(data.indexOf(it)!=data.size-1) {
                    Divider(
                        thickness = 1.dp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
                if(data.indexOf(it)==data.size-1) {
                    Spacer(
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }
    }

}