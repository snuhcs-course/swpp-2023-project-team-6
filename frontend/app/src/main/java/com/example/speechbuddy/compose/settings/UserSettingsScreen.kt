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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonLevel
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.viewmodel.UserSettingsViewModel

@Composable
fun UserSettingsScreen(
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
            onConfirmButtonClick = { viewModel.logout() },
            onDismissButtonClick = { viewModel.onHideLogoutDialog() },
            title = stringResource(id = R.string.settings_account_logout),
            content = stringResource(id = R.string.logout_warning),
            confirmButtonText = stringResource(id = R.string.logout_confirm),
            dismissButtonText = stringResource(id = R.string.logout_dismiss)
        )
    }

    if (viewModel.showWithdrawalDialog) {
        AlertDialogUi(
            onConfirmButtonClick = {
                viewModel.onShowSecondWithdrawalDialog()
                viewModel.onHideWithdrawalDialog()
            },
            onDismissButtonClick = { viewModel.onHideWithdrawalDialog() },
            title = stringResource(id = R.string.settings_account_withdrawal),
            content = stringResource(id = R.string.withdrawal_warning),
            confirmButtonText = stringResource(id = R.string.withdrawal_confirm),
            dismissButtonText = stringResource(id = R.string.withdrawal_dismiss)
        )
    }

    if (viewModel.showSecondWithdrawalDialog) {
        //onHideWithdrawalDialog
        AlertDialogUi(
            onConfirmButtonClick = { viewModel.deleteAccount() },
            onDismissButtonClick = { viewModel.onHideSecondWithdrawalDialog() },
            title = stringResource(id = R.string.settings_account_withdrawal),
            content = stringResource(id = R.string.withdrawal_second_warning),
            confirmButtonText = stringResource(id = R.string.withdrawal_second_confirm),
            dismissButtonText = stringResource(id = R.string.withdrawal_second_dismiss)
        )
    }


}

@Preview
@Composable
private fun UserSettingsScreenPreview() {
    SpeechBuddyTheme {
        UserSettingsScreen(
            modifier = Modifier,
            onBackClick = {},
            email = "id",
            nickname = "nickname"
        )
    }
}