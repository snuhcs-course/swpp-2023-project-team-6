package com.example.speechbuddy.compose.settings

import SettingsTextUi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun DeveloperInfoScreen(
    modifier: Modifier,
    onBackClick: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier.padding(start = 30.dp, end = 30.dp, top = 10.dp)
        ) {
            BackButtonUi(onBackClick = onBackClick)
            Spacer(modifier = modifier.height(136.dp))
            SettingsTitleUi(
                modifier = modifier,
                title = stringResource(id = R.string.settings_developerinfo_button)
            )
            Spacer(modifier = modifier.height(20.dp))
            Text(
                modifier = modifier,
                text = "서울대학교 소개원실 team6",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = modifier.height(20.dp))
            SettingsTextUi(modifier = modifier, text = "김연정")
            Spacer(modifier = modifier.height(20.dp))
            SettingsTextUi(modifier = modifier, text = "류명현")
            Spacer(modifier = modifier.height(20.dp))
            SettingsTextUi(modifier = modifier, text = "오준형")
            Spacer(modifier = modifier.height(20.dp))
            SettingsTextUi(modifier = modifier, text = "이민영")
            Spacer(modifier = modifier.height(20.dp))
            SettingsTextUi(modifier = modifier, text = "이석찬")
            Spacer(modifier = modifier.height(20.dp))
            SettingsTextUi(modifier = modifier, text = "주승민")
        }
    }
}

@Preview
@Composable
fun DeveloperInfoScreenPreview() {
    SpeechBuddyTheme {
        DeveloperInfoScreen(modifier = Modifier, onBackClick = { /*TODO*/ })
    }
}