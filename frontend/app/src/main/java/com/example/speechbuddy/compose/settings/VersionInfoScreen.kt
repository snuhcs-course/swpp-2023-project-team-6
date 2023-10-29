package com.example.speechbuddy.compose.settings

import SettingsTextUi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun VersionInfoScreen(
    modifier: Modifier,
    onBackClick: () -> Unit,
    versionText: String
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier.padding(vertical = 30.dp, horizontal = 15.dp)
        ) {
            BackButtonUi(onBackClick = onBackClick)
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            SettingsTitleUi(title = stringResource(id = R.string.settings_versioninfo_button))

            Spacer(modifier = modifier.height(20.dp))

            Row(
                modifier = modifier.fillMaxWidth()
            ) {
                SettingsTextUi(text = stringResource(id = R.string.version))
                Spacer(modifier.weight(1f))
                SettingsTextUi(text = versionText)
            }

            Spacer(modifier = modifier.height(20.dp))

            Row(
                modifier = modifier.fillMaxWidth(),
            ) {
                SettingsTextUi(text = stringResource(id = R.string.email_field))
                Spacer(modifier.weight(1f))
                SettingsTextUi(text = stringResource(id = R.string.developer_email))
            }
        }
    }
}

@Preview
@Composable
fun VersionInfoScreenPreview() {
    SpeechBuddyTheme {
        VersionInfoScreen(modifier = Modifier, onBackClick = { /*TODO*/ }, versionText = "1.0.0")
    }
}