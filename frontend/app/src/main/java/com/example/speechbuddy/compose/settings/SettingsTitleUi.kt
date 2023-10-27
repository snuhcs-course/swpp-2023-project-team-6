package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun SettingsTitleUi(
    modifier: Modifier,
    title: String
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = title,
        style = MaterialTheme.typography.displaySmall,
    )
}

@Preview
@Composable
fun SettingsTitleUiPreview(){
    SpeechBuddyTheme{
        SettingsTitleUi(modifier = Modifier, title = "title")
    }
}