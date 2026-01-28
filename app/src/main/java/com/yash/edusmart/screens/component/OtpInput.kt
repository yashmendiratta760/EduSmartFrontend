package com.yash.edusmart.login_signup.screens.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yash.edusmart.viewmodel.LoginUiState

@Composable
fun OtpInput(
    isDarkTheme:Boolean,
    onOtpComplete: (String) -> Unit
) {
    val otpLength = 6
    val focusRequesters = List(otpLength) { FocusRequester() }
    val otpValues = remember { mutableStateListOf("", "", "", "", "", "") }





    Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxWidth()) {
        for (i in 0 until otpLength) {
            TextField(
                value = otpValues[i],
                onValueChange = { value ->
                    if (value.length <= 1 && value.all { it.isDigit() }) {
                        otpValues[i] = value
                        if (value.isNotEmpty() && i < otpLength - 1) {
                            focusRequesters[i + 1].requestFocus()
                        }
                        if (otpValues.all { it.length == 1 }) {
                            onOtpComplete(otpValues.joinToString(""))
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = if(!isDarkTheme) Color(0xFF292C31)
                    else Color(0xFF515152),
                    unfocusedContainerColor = if(!isDarkTheme) Color(0xFF292C31)
                    else Color(0xFF515152)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .width(48.dp)
                    .focusRequester(focusRequesters[i])
                    .focusProperties {
                        if (i > 0) previous = focusRequesters[i - 1]
                        if (i < otpLength - 1) next = focusRequesters[i + 1]
                    },
                singleLine = true,
                maxLines = 1,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            )
        }
    }
}

