package com.example.speechbuddy.compose.utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProgressIndicatorUi(){
    CircularProgressIndicator(
        modifier = Modifier.fillMaxSize().wrapContentSize(),
        color = MaterialTheme.colorScheme.primary
    )
}