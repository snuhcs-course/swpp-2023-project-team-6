package com.example.speechbuddy.compose.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.speechbuddy.R
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun TitleUi(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
) {
    Column {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = MaterialTheme.typography.displayMedium,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = description,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TitleUiPreview() {
    SpeechBuddyTheme {
        TitleUi(
            title = stringResource(id = R.string.signup_text),
            description = stringResource(id = R.string.signup_explain)
        )
    }
}