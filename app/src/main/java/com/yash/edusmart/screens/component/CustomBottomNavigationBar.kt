package com.yash.edusmart.screens.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CustomBottomNavigationBar(
    selectedIndex:Int,
    onItemSelected:(Int)->Unit,
    items:List<Triple<String, ImageVector, ImageVector>>
)
{

    NavigationBar(
        contentColor = Color.White,
        containerColor = Color(0xFF1C1C23)
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = {
                    Box(contentAlignment = Alignment.Center,
                        modifier = Modifier)
                    {
                        if (selectedIndex == index) Circle()
                        Column {
                            Icon(
                                if (selectedIndex != index) item.second
                                else item.third,
                                modifier = Modifier.size(30.dp),
                                contentDescription = item.first
                            )
                        }
                    }
                },
                label = {
                    Text(text = item.first, fontSize = 10.sp)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFC8F1F1),
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.White
                )
            )
        }
    }

}

@Composable
fun Circle(modifier: Modifier = Modifier)
{
    Box(modifier = modifier
        .size(50.dp)
        .background(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.2f)
        ))
}