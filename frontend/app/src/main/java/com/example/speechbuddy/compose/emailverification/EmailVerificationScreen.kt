package com.example.speechbuddy.compose.emailverification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonLevel
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TextFieldUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.ui.models.EmailVerificationErrorType
import com.example.speechbuddy.viewmodel.EmailVerificationViewModel

@Composable
fun EmailVerificationScreen(
    source: String?,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: EmailVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEmailError = uiState.error?.type == EmailVerificationErrorType.EMAIL
    val isVerifyCodeError = uiState.error?.type == EmailVerificationErrorType.VERIFY_CODE
    val isError = (isEmailError || isVerifyCodeError) &&
            (uiState.error?.messageId != R.string.connection_error)

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TitleUi(
                title = when (source) {
                    "reset_password" -> stringResource(id = R.string.reset_password)
                    "signup" -> stringResource(id = R.string.signup)
                    else -> stringResource(id = R.string.email_verification)
                }, description = when (source) {
                    "reset_password" -> stringResource(id = R.string.verify_email_for_reset_password)
                    "signup" -> stringResource(id = R.string.verify_email_for_signup)
                    else -> stringResource(id = R.string.email_verification_description)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Email Text Field
            TextFieldUi(
                value = viewModel.emailInput,
                onValueChange = { viewModel.setEmail(it) },
                label = { Text(text = stringResource(id = R.string.email)) },
                supportingButton = {
                    ButtonUi(
                        text = stringResource(id = R.string.send_code),
                        onClick = { viewModel.sendCode(source) },
                        level = ButtonLevel.TERTIARY
                    )
                },
                supportingText = {
                    if (isEmailError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    } else if (uiState.isSuccessfulSend) {
                        Text(stringResource(id = R.string.code_successfully_sent))
                    }
                },
                isError = isError,
                isValid = uiState.isValidEmail,
                // If verification email is sent successfully, a user cannot change one's email input
                isEnabled = !uiState.isSuccessfulSend
            )

            // Verify code Text Field
            TextFieldUi(
                value = viewModel.verifyCodeInput,
                onValueChange = { viewModel.setVerifyCode(it) },
                label = { Text(text = stringResource(id = R.string.code)) },
                supportingText = {
                    if (isVerifyCodeError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    }
                },
                isError = isError,
                isValid = uiState.isValidVerifyCode,
                isEnabled = uiState.isSuccessfulSend
            )

            Spacer(modifier = Modifier.height(20.dp))

            ButtonUi(
                text = stringResource(id = R.string.next),
                onClick = { viewModel.verifyEmail(source, navController) },
                isEnabled = uiState.isSuccessfulSend
            )
        }
    }
}