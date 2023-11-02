package com.example.speechbuddy.compose.settings

import SettingsTextUi
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.HomeTopAppBarUi
import com.example.speechbuddy.viewmodel.BackupViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupSettings(
    modifier: Modifier,
    onBackClick: () -> Unit,
    lastBackupDate: String,
    viewModel: BackupViewModel = hiltViewModel()
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                HomeTopAppBarUi(
                    title = stringResource(id = R.string.settings),
                    onBackClick = onBackClick,
                    isBackClickEnabled = true
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
            ) {
                SettingsTitleUi(title = stringResource(id = R.string.settings_backup_button))

                Spacer(modifier = modifier.height(20.dp))

                Row(
                    modifier = modifier.fillMaxWidth()
                ) {
                    SettingsTextUi(text = stringResource(id = R.string.last_backup_date))

                    Spacer(modifier.weight(1f))

                    SettingsTextUi(text = lastBackupDate)
                }

                Spacer(modifier = modifier.height(10.dp))

                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SettingsTextUi(text = stringResource(id = R.string.automatic_backup_enable))

                    Spacer(modifier.weight(1f))

                    Switch(
                        checked = viewModel.automaticBackupChecked,
                        onCheckedChange = { viewModel.onAutomaticBackupCheckedChange() }
                    )
                }

                Spacer(modifier = modifier.height(165.dp))

                ButtonUi(
                    modifier = modifier,
                    text = stringResource(id = R.string.backup_now),
                    onClick = { viewModel.backup() }
                )
            }
        }
    }
}