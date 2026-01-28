package com.yash.edusmart.screens.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yash.edusmart.R

@Composable
fun ChatCard(@DrawableRes image: Int,
             name: String,
             onClick:()-> Unit){
    Box(modifier = Modifier.fillMaxWidth()
        .clickable(onClick = onClick)
        .clip(shape = CircleShape),
        contentAlignment = Alignment.CenterStart){
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(70.dp)){
                Image(painter = painterResource(image), contentDescription = "pfp")
            }
            Text(text = name, fontSize = 18.sp)
        }
    }
}
//@Preview(showBackground = true)
//@Composable
//private fun previewc() {
//    ChatCard(image = R.drawable.google_logo,
//        name = "Yasuuuuuu")
//}