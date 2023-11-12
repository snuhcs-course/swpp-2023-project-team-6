package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.AlertDialogUi
import com.example.speechbuddy.compose.utils.ButtonLevel
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TopAppBarUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.ui.models.AccountSettingsAlert
import com.example.speechbuddy.viewmodel.AccountSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettings(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    bottomPaddingValues: PaddingValues,
    viewModel: AccountSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBarUi(
                    title = stringResource(id = R.string.settings),
                    onBackClick = onBackClick,
                    isBackClickEnabled = true
                )
            }
        ) { topPaddingValues ->
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
                TitleUi(title = stringResource(id = R.string.account))

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SettingsRow(
                        label = stringResource(id = R.string.email),
                        content = {
                            SettingsRowText(text = uiState.email)
                        }
                    )

                    SettingsRow(
                        label = stringResource(id = R.string.nickname),
                        content = {
                            SettingsRowText(text = uiState.nickname)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ButtonUi(
                        text = stringResource(id = R.string.logout),
                        onClick = { viewModel.showAlert(AccountSettingsAlert.LOGOUT) }
                    )

                    ButtonUi(
                        text = stringResource(id = R.string.withdraw),
                        onClick = { viewModel.showAlert(AccountSettingsAlert.WITHDRAW) },
                        level = ButtonLevel.QUATERNARY
                    )
                }
            }
        }
    }

    uiState.alert?.let { alert ->
        when (alert) {
            AccountSettingsAlert.LOGOUT -> {
                AlertDialogUi(
                    title = stringResource(id = R.string.logout),
                    text = stringResource(id = R.string.logout_warning),
                    dismissButtonText = stringResource(id = R.string.cancel),
                    confirmButtonText = stringResource(id = R.string.logout),
                    onDismiss = { viewModel.hideAlert() },
                    onConfirm = { viewModel.logout() }
                )
            }

            AccountSettingsAlert.WITHDRAW -> {
                AlertDialogUi(
                    title = stringResource(id = R.string.withdraw),
                    text = stringResource(id = R.string.withdraw_warning),
                    dismissButtonText = stringResource(id = R.string.cancel),
                    confirmButtonText = stringResource(id = R.string.proceed),
                    onDismiss = { viewModel.hideAlert() },
                    onConfirm = {
                        viewModel.showAlert(AccountSettingsAlert.WITHDRAW_PROCEED)
                    }
                )
            }

            AccountSettingsAlert.WITHDRAW_PROCEED -> {
                AlertDialogUi(
                    title = stringResource(id = R.string.withdraw),
                    text = stringResource(id = R.string.withdraw_proceed_warning),
                    dismissButtonText = stringResource(id = R.string.cancel),
                    confirmButtonText = stringResource(id = R.string.withdraw),
                    onDismiss = { viewModel.hideAlert() },
                    onConfirm = { viewModel.deleteAccount() }
                )
            }
        }
    }
}