package com.example.speechbuddy.compose.landing

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {}
}

@Preview
@Composable
private fun LandingScreenPreview() {
    SpeechBuddyTheme {
        LandingScreen(onLoginClick = {})
    }
}