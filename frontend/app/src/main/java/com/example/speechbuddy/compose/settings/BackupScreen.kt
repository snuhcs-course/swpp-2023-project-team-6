package com.example.speechbuddy.compose.settings

import SettingsTextUi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun BackupScreen(
    modifier: Modifier,
    onBackClick: () -> Unit,
    onBackupClick: () -> Unit,
    lastBackupDate: String,
    automaticBackupChecked: Boolean,
    onAutomaticBackupCheckedChange: (Boolean) -> Unit
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
                title = stringResource(id = R.string.settings_backup_button)
            )
            Spacer(modifier = modifier.height(20.dp))
            Row(
                modifier = modifier.fillMaxWidth()
            ) {
                SettingsTextUi(
                    modifier = modifier,
                    text = stringResource(id = R.string.last_backup_date)
                )
                Spacer(modifier.weight(1f))
                SettingsTextUi(modifier = modifier, text = lastBackupDate)
            }
            Spacer(modifier = modifier.height(10.dp))
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SettingsTextUi(
                    modifier = modifier,
                    text = stringResource(id = R.string.automatic_backup_enable)
                )
                Spacer(modifier.weight(1f))
                Switch(
                    checked = automaticBackupChecked,
                    onCheckedChange = onAutomaticBackupCheckedChange
                )
            }
            Spacer(modifier = modifier.height(165.dp))
            ButtonUi(
                modifier = modifier,
                text = stringResource(id = R.string.backup_now),
                onClick = onBackupClick
            )
        }
    }
}

@Preview
@Composable
fun BackupScreenPreview() {
    SpeechBuddyTheme {
        BackupScreen(
            modifier = Modifier,
            onBackClick = {},
            onBackupClick = {},
            lastBackupDate = "2023.10.27",
            automaticBackupChecked = true,
            onAutomaticBackupCheckedChange = {})
    }
}