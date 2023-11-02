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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.AlertDialogUi
import com.example.speechbuddy.compose.utils.ButtonLevel
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.viewmodel.UserSettingsViewModel

@Composable
fun AccountSettings(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    email: String,
    nickname: String,
    viewModel: UserSettingsViewModel = hiltViewModel()
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
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
            SettingsTitleUi(title = stringResource(id = R.string.settings_account_button))

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = modifier.fillMaxWidth(),
            ) {
                SettingsTextUi(text = stringResource(id = R.string.email_field))
                Spacer(modifier.weight(1f))
                SettingsTextUi(text = email)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = modifier.fillMaxWidth(),
            ) {
                SettingsTextUi(text = stringResource(id = R.string.nickname))
                Spacer(modifier.weight(1f))
                SettingsTextUi(text = nickname)
            }

            Spacer(modifier = Modifier.height(130.dp))

            ButtonUi(
                text = stringResource(id = R.string.settings_account_logout),
                onClick = { viewModel.onShowLogoutDialog() },
                level = ButtonLevel.PRIMARY
            )

            Spacer(modifier = Modifier.height(20.dp))

            ButtonUi(
                text = stringResource(id = R.string.settings_account_withdrawal),
                onClick = { viewModel.onShowWithdrawalDialog() },
                level = ButtonLevel.INVERSESURFACE
            )
        }
    }

    if (viewModel.showLogoutDialog) {
        AlertDialogUi(
            title = stringResource(id = R.string.settings_account_logout),
            text = stringResource(id = R.string.logout_warning),
            dismissButtonText = stringResource(id = R.string.logout_dismiss),
            confirmButtonText = stringResource(id = R.string.logout_confirm),
            onDismiss = { viewModel.onHideLogoutDialog() },
            onConfirm = { viewModel.logout() }
        )
    }

    if (viewModel.showWithdrawalDialog) {
        AlertDialogUi(
            title = stringResource(id = R.string.settings_account_withdrawal),
            text = stringResource(id = R.string.withdrawal_warning),
            dismissButtonText = stringResource(id = R.string.withdrawal_dismiss),
            confirmButtonText = stringResource(id = R.string.withdrawal_confirm),
            onDismiss = { viewModel.onHideWithdrawalDialog() },
            onConfirm = {
                viewModel.onShowSecondWithdrawalDialog()
                viewModel.onHideWithdrawalDialog()
            }
        )
    }

    if (viewModel.showSecondWithdrawalDialog) {
        AlertDialogUi(
            title = stringResource(id = R.string.settings_account_withdrawal),
            text = stringResource(id = R.string.withdrawal_second_warning),
            dismissButtonText = stringResource(id = R.string.withdrawal_second_dismiss),
            confirmButtonText = stringResource(id = R.string.withdrawal_second_confirm),
            onDismiss = { viewModel.onHideSecondWithdrawalDialog() },
            onConfirm = { viewModel.deleteAccount() }
        )
    }
}