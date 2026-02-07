package com.yash.edusmart.screens.component

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HeartBroken
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yash.edusmart.R

@Composable
fun ItemCard(image: Int,
             title: String,
             sellType: String,
             price:Int? = null)
{
    Box(modifier = Modifier.background(color = Color.White),
        contentAlignment = Alignment.Center)
    {
        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .height(200.dp) // adjust as needed
                .padding(8.dp)
        ) {
            // Heart icon at top-right
            Icon(
                imageVector = Icons.Outlined.HeartBroken,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(image)
                    , modifier = Modifier.size(140.dp),
                    contentDescription = null
                )
                Text(text = title)
                if(price!=null)
                {
                    Text(text = price.toString())
                }
                Text(text = sellType)
            }

    }

}
@Preview(showSystemUi = true)
@Composable
fun laxy()
{
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(6){
            ItemCard(image = R.drawable.logo__1_,
                "HelloWorld","Rent")
        }
    }
}
