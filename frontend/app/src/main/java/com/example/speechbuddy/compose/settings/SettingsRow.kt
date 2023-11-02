package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun SettingsRow(
    modifier: Modifier = Modifier,
    label: String,
    content: @Composable (() -> Unit)
) {
    Row(
        modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
        )

        content()
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsRowPreview() {
    SpeechBuddyTheme {
        SettingsRow(
            label = "이메일",
            content = {
                Text(
                    text = "example@gmail.com",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}