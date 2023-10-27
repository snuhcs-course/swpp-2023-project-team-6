package com.example.speechbuddy.compose.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun BackButtonUi(
    onBackClick: () -> Unit
){
    IconButton(onClick = onBackClick) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Go back to settings page"
        )
    }
}

@Preview
@Composable
fun BackButtonUiPreview(){
    SpeechBuddyTheme{
        BackButtonUi(
            onBackClick = {}
        )
    }
}