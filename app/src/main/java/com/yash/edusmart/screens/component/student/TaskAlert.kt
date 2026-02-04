package com.yash.edusmart.screens.component.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
@Composable
fun TaskAlert(
    heading: String,
    isStudent: Boolean,
    isTeacher: Boolean = false,
    task: String,
    deadline: String,
    checked: Boolean = false,
    completedByNames: List<String> = emptyList(),
    onCheckedChange: (Boolean) -> Unit = {},
    onSubmit: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0F2E55).copy(alpha = 0.5f),
                        Color(0xFF1F4B80)
                    )
                ),
                shape = RoundedCornerShape(30.dp)
            )
    ) {

        // ✅ Delete icon pinned to top-right corner
        if (!isStudent) {
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding( 20.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "DELETE", tint = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)

        ) {


            Text(
                text = heading,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.White
            )
            Text(
                text = "Deadline: $deadline",
                fontSize = 14.sp,
                color = Color.White
            )


            Text(
                text = task,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(top = 10.dp)
            )

            if (isStudent) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { onCheckedChange(it) }
                        )
                        Text("Completed", color = Color.White)
                    }

                    Button(
                        onClick = onSubmit,
                        enabled = checked
                    ) {
                        Text("Submit")
                    }
                }
            }

            if (isTeacher) {
                CompletedByExpandable(completedBy = completedByNames)
            }
        }
    }
}

@Composable
fun CompletedByExpandable(
    completedBy: List<String>, // names (or emails)
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Completed by (${completedBy.size})",
                fontWeight = FontWeight.SemiBold
            )
            Text(text = if (expanded) "▲" else "▼")
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                if (completedBy.isEmpty()) {
                    Text("No one has completed yet.")
                } else {
                    completedBy.forEach { name ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("• $name")
                        }
                    }
                }
            }
        }
    }
}

