package com.yash.edusmart.screens.teacher

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.yash.edusmart.login_signup.screens.component.CustomTextField
import com.yash.edusmart.screens.component.CustomDropdownMenu

@Composable
fun TimeTableShow(){
    val selectedBranch = remember { mutableStateOf("Select Branch") }
    val branches = listOf("CSE","IT","ECE")
    val semester = listOf("1","2","3","4","5","6","7","8")
    val selectedSem = remember { mutableStateOf("Select Semester") }
    val selectedSubject = remember { mutableStateOf("Select Subject") }
    val subjects = listOf("DS","DLCD")
    Column {
        CustomDropdownMenu(
            options = branches,
            selectedOption = selectedBranch
        ) {branch->
            selectedBranch.value = branch
        }

        CustomDropdownMenu(
            options = semester,
            selectedOption = selectedSem
        ) {sem->
            selectedSem.value = sem
        }
        CustomDropdownMenu(
            options = subjects,
            selectedOption = selectedSubject
        ) {subject->
            selectedSubject.value = subject
        }

    }
}