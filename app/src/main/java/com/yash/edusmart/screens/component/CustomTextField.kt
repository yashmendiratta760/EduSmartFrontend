package com.yash.edusmart.login_signup.screens.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.math.truncate

@Composable
fun CustomTextField(modifier: Modifier = Modifier
                    ,value:String,
                    onValueChange:(String)->Unit,
                    label:String,
                    leadingIcon:@Composable (()->Unit)?=null,
                    keyboardOptions:KeyboardOptions,
                    focusedContainerColor:Color,
                    unfocusedContainerColor:Color
                    )
{
    TextField(value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(30.dp),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        modifier = modifier.padding(start = 10.dp,end = 10.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = focusedContainerColor,
            unfocusedContainerColor = unfocusedContainerColor)
    )

}