package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.ui.SpeechBuddyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onAccountClick: () -> Unit,
    onBackupClick: () -> Unit,
    onDisplayClick: () -> Unit,
    onManageSymbolClick: () -> Unit,
    onVersionInfoClick: () -> Unit,
    onDeveloperInfoClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(25.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            SettingsTextButtonUi(
                text = stringResource(id = R.string.settings_account_button),
                onClick = onAccountClick
            )
            SettingsTextButtonUi(
                text = stringResource(id = R.string.settings_backup_button),
                onClick = onBackupClick
            )
            SettingsTextButtonUi(
                text = stringResource(id = R.string.settings_display_button),
                onClick = onDisplayClick
            )
            SettingsTextButtonUi(
                text = stringResource(id = R.string.settings_manage_symbol_button),
                onClick = onManageSymbolClick
            )
            SettingsTextButtonUi(
                text = stringResource(id = R.string.settings_versioninfo_button),
                onClick = onVersionInfoClick
            )
            SettingsTextButtonUi(
                text = stringResource(id = R.string.settings_developerinfo_button),
                onClick = onDeveloperInfoClick
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    SpeechBuddyTheme {
        SettingsScreen(
            onAccountClick = {},
            onBackupClick = {},
            onDisplayClick = {},
            onManageSymbolClick = {},
            onVersionInfoClick = {},
            onDeveloperInfoClick = {},
            onBackClick = {}
        )
    }
}