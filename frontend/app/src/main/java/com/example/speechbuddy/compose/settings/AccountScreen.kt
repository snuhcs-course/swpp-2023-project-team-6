package com.example.speechbuddy.compose.settings

import SettingsTextUi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonLevel
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    showLogoutDialog: Boolean,
    onShowLogoutDialog: () -> Unit,
    onHideLogoutDialog: () -> Unit,
    showWithdrawalDialog: Boolean,
    onShowWithdrawalDialog: () -> Unit,
    onHideWithdrawalDialog: () -> Unit,
    showSecondWithdrawalDialog: Boolean,
    onShowSecondWithdrawalDialog: () -> Unit,
    onHideSecondWithdrawalDialog: () -> Unit,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp, top = 30.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            BackButtonUi(onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(136.dp))

            SettingsTitleUi(
                modifier = modifier,
                title = stringResource(id = R.string.settings_account_button)
            )

            Spacer(modifier = Modifier.height(20.dp))

            SettingsTextUi(
                modifier = modifier,
                text = stringResource(id = R.string.nickname)
            )

            Spacer(modifier = Modifier.height(20.dp))

            SettingsTextUi(
                modifier = modifier,
                text = stringResource(id = R.string.email_field)
            )

            Spacer(modifier = Modifier.height(165.dp))

            ButtonUi(
                text = stringResource(id = R.string.settings_account_logout),
                onClick = { onShowLogoutDialog() },
                level = ButtonLevel.PRIMARY
            )

            Spacer(modifier = Modifier.height(20.dp))

            ButtonUi(
                text = stringResource(id = R.string.settings_account_withdrawal),
                onClick = { onShowWithdrawalDialog() },
                level = ButtonLevel.INVERSESURFACE
            )
        }
    }
    if (showLogoutDialog) {
        AlertDialogUi(
            modifier = modifier,
            onConfirmButtonClick = {},
            onDismissButtonClick = { onHideLogoutDialog() },
            title = stringResource(id = R.string.settings_account_logout),
            content = stringResource(id = R.string.logout_warning),
            confirmButtonText = stringResource(id = R.string.logout_confirm),
            dismissButtonText = stringResource(id = R.string.logout_dismiss)
        )
    }
    if (showWithdrawalDialog) {
        AlertDialogUi(
            modifier = modifier,
            onConfirmButtonClick = {
                onShowSecondWithdrawalDialog()
                onHideWithdrawalDialog()
            },
            onDismissButtonClick = { onHideWithdrawalDialog() },
            title = stringResource(id = R.string.settings_account_withdrawal),
            content = stringResource(id = R.string.withdrawal_warning),
            confirmButtonText = stringResource(id = R.string.withdrawal_confirm),
            dismissButtonText = stringResource(id = R.string.withdrawal_dismiss)
        )
    }
    if (showSecondWithdrawalDialog) {
        //onHideWithdrawalDialog
        AlertDialogUi(
            modifier = modifier,
            onConfirmButtonClick = {},
            onDismissButtonClick = { onHideSecondWithdrawalDialog() },
            title = stringResource(id = R.string.settings_account_withdrawal),
            content = stringResource(id = R.string.withdrawal_second_warning),
            confirmButtonText = stringResource(id = R.string.withdrawal_second_confirm),
            dismissButtonText = stringResource(id = R.string.withdrawal_second_dismiss)
        )
    }


}

@Preview
@Composable
private fun AccountScreenPreview() {
    SpeechBuddyTheme {
        AccountScreen(
            modifier = Modifier,
            showLogoutDialog = false,
            onShowLogoutDialog = {},
            onHideLogoutDialog = {},
            showWithdrawalDialog = false,
            onShowWithdrawalDialog = {},
            onHideWithdrawalDialog = {},
            showSecondWithdrawalDialog = false,
            onShowSecondWithdrawalDialog = {},
            onHideSecondWithdrawalDialog = {},
            onBackClick = {}
        )
    }
}