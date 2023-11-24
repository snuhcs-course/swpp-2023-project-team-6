package com.example.speechbuddy.compose.settings

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TopAppBarUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.ui.models.BackupSettingsAlert
import com.example.speechbuddy.viewmodel.BackupSettingsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupSettings(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    bottomPaddingValues: PaddingValues,
    viewModel: BackupSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val loading by viewModel.loading.observeAsState()

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(topBar = {
            TopAppBarUi(
                title = stringResource(id = R.string.settings),
                onBackClick = onBackClick,
                isBackClickEnabled = true
            )
        }) { topPaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = topPaddingValues.calculateTopPadding(),
                        bottom = bottomPaddingValues.calculateBottomPadding()
                    )
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                TitleUi(title = stringResource(id = R.string.backup_to_server))

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SettingsRow(
                        label = stringResource(id = R.string.last_backup_date),
                        content = {
                            SettingsRowText(text = uiState.lastBackupDate)
                        }
                    )

                    SettingsRow(label = stringResource(id = R.string.enable_auto_backup),
                        content = {
                            Switch(
                                checked = uiState.isAutoBackupEnabled,
                                onCheckedChange = { viewModel.setAutoBackup(it) },
                                modifier = Modifier.heightIn(max = 32.dp)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))

                ButtonUi(
                    text = stringResource(id = R.string.backup_now),
                    onClick = { viewModel.backup() }
                )
            }
        }
    }

    uiState.alert.let { alert ->
        when (alert) {
            BackupSettingsAlert.SUCCESS -> {
                viewModel.toastDisplayed()
                Toast.makeText(
                    LocalContext.current,
                    stringResource(id = R.string.backup_success),
                    Toast.LENGTH_SHORT
                ).show()
            }

            BackupSettingsAlert.CONNECTION -> {
                viewModel.toastDisplayed()
                Toast.makeText(
                    LocalContext.current,
                    stringResource(id = R.string.connection_error),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }

    if (loading == true) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
        )
    }
}