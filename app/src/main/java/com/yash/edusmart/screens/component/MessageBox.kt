package com.yash.edusmart.screens.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yash.edusmart.viewmodel.ChatViewModel
import org.json.JSONObject

@Composable
fun Messagebox(modifier: Modifier=Modifier,
               message:String?,
               isSent:Boolean,
               id:Int,
               name: String,
               time:String,
               viewModel: ChatViewModel,
               imageUri:String? = null)
{
    var isImageOpen by remember { mutableStateOf(false)}
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isSent) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            // viewModel.deleteMessageById(id)
                        },
                        onTap = {
                            if (imageUri != null) {
                                isImageOpen = true
                            }
                        }
                    )
                }
                .widthIn(max = (LocalConfiguration.current.screenWidthDp / 1.6f).dp)
                .background(
                    color = if (isSent) Color(0xFFD1FCD3) else Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isSent) 16.dp else 0.dp,
                        bottomEnd = if (isSent) 0.dp else 16.dp
                    )
                )
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {

            // ✅ NAME at top
            Text(
                text = name,
                fontSize = 11.sp,
                color = Color.Gray,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                modifier = Modifier
                    .align(
                        if (isSent) Alignment.TopEnd else Alignment.TopStart
                    )
                    .padding(bottom = 30.dp)
            )

            // ✅ TIME at bottom-right
            Text(
                text = time,
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(top = 6.dp)
            )


            if (message != null) {
                Text(
                    text = message,
                    fontSize = 16.sp,
                    color = if (isSent) Color.Black else Color.DarkGray,

                    modifier = Modifier.padding(start = 1.dp,
                        end = 16.dp,
                        top = 20.dp,
                        bottom = 8.dp)
                )
            }
            else if(imageUri!=null)
            {



            }
        }
    }
    if (isImageOpen) {
        Dialog(onDismissRequest = { isImageOpen = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { isImageOpen = false }
            ) {
            }
        }
    }
}