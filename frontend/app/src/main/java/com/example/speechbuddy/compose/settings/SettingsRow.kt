package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun SettingsRow(
    modifier: Modifier = Modifier,
    label: String,
    content: @Composable (() -> Unit) = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SettingsRowText(text = label)
        content()
    }
}

@Composable
fun SettingsRowText(
    modifier: Modifier = Modifier,
    text: String
) {
    Box(
        modifier = modifier.height(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsRowPreview() {
    SpeechBuddyTheme {
        SettingsRow(
            label = "이메일",
            content = {
                SettingsRowText(text = "example@gmail.com")
            }
        )
    }
}