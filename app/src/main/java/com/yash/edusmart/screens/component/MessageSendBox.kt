package com.yash.edusmart.screens.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yash.edusmart.login_signup.screens.component.CustomTextField


@Composable
fun MessageSendBox(modifier: Modifier= Modifier,
                   isDarkTheme: Boolean=isSystemInDarkTheme(),
                   value: String,
                   onValueChange:(String)->Unit,
                   onSendClick:()-> Unit){
    Row (modifier = modifier.fillMaxWidth()){
        CustomTextField(
            value = value,
            modifier = Modifier.weight(0.6f),
            onValueChange = onValueChange,
            label = "Send Message",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            focusedContainerColor = if (!isDarkTheme) Color(0xFF292C31)
            else Color(0xFF515152),
            unfocusedContainerColor = if (!isDarkTheme) Color(0xFF292C31)
            else Color(0xFF515152)
        )
        Box(modifier = Modifier.size(55.dp)
            .background(shape = CircleShape, color = Color.Green)
            .clickable(onClick = onSendClick),
            contentAlignment = Alignment.Center){
            Icon(Icons.Default.Send, contentDescription = "Send")
        }
    }
}