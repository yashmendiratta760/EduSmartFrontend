package com.yash.edusmart.login_signup.screens.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun LoginSignupBackground(isDarkTheme:Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if(!isDarkTheme) Color(0xFFE6F0FA) else Color(0xFF2E3039)) // light blue background
    ) {
        // Top-left circle
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset((-40).dp, (-40).dp) // move outside screen
                .background(if(!isDarkTheme)Color(0xFF1565C0)
                else Color(0xFF7CADE6)
                    , shape = CircleShape)
        )

        // Top-right big circle
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.TopEnd)
                .offset(50.dp, (-80).dp)
                .background(if(!isDarkTheme)Color(0xFF1565C0)
                else Color(0xFF7CADE6)
                    , shape = CircleShape)
        )

        // Bottom-left big circle
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.BottomStart)
                .offset((-60).dp, 60.dp)
                .background(if(!isDarkTheme)Color(0xFF1565C0)
                else Color(0xFF7CADE6)
                    , shape = CircleShape)
        )

        // Small bottom-left circle
        Box(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.BottomStart)
                .offset(140.dp,-120.dp)
                .background(if(!isDarkTheme)Color(0xFF1565C0)
                else Color(0xFF7CADE6)
                    , shape = CircleShape)
        )
    }
}
