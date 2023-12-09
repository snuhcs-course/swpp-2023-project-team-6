package com.example.speechbuddy.compose.emailverification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
    navigateCallback: (String) -> Unit,
    viewModel: EmailVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val isEmailError = uiState.error?.type == EmailVerificationErrorType.EMAIL
    val isCodeError = uiState.error?.type == EmailVerificationErrorType.CODE
    val isConnectionError = uiState.error?.type == EmailVerificationErrorType.CONNECTION
    val isError = (isEmailError || isCodeError) && !isConnectionError

    LaunchedEffect(Unit) {
        viewModel.setSource(source)
    }

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
            when (source) {
                "reset_password" -> TitleUi(
                    title = stringResource(id = R.string.reset_password),
                    description = stringResource(id = R.string.verify_email_for_reset_password)
                )

                "signup" -> TitleUi(
                    title = stringResource(id = R.string.signup),
                    description = stringResource(id = R.string.verify_email_for_signup)
                )

                else -> TitleUi(
                    title = stringResource(id = R.string.email_verification),
                    description = stringResource(id = R.string.email_verification_description)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextFieldUi(
                value = viewModel.emailInput,
                onValueChange = { viewModel.setEmail(it) },
                isEnabled = !uiState.isCodeSuccessfullySent,
                label = { Text(text = stringResource(id = R.string.email)) },
                supportingButton = {
                    ButtonUi(
                        text = stringResource(id = R.string.send_code),
                        onClick = { viewModel.sendCode() },
                        isEnabled = !isEmailError && uiState.buttonEnabled,
                        level = ButtonLevel.TERTIARY
                    )
                },
                supportingText = {
                    if (isEmailError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    } else if (uiState.isCodeSuccessfullySent) {
                        Text(stringResource(id = R.string.code_successfully_sent))
                    }
                },
                isError = isError,
                isValid = uiState.isValidEmail,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    autoCorrect = true
                )
            )

            TextFieldUi(
                value = viewModel.codeInput,
                onValueChange = { viewModel.setCode(it) },
                label = { Text(text = stringResource(id = R.string.code)) },
                supportingText = {
                    if (isCodeError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    } else if (isConnectionError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    }
                },
                isError = isError,
                isValid = uiState.isValidCode
            )

            Spacer(modifier = Modifier.height(20.dp))

            ButtonUi(
                text = stringResource(id = R.string.next),
                onClick = { viewModel.verifyEmail(navigateCallback = navigateCallback) },
                isEnabled = uiState.isCodeSuccessfullySent && uiState.buttonEnabled
            )
        }
    }

    uiState.loading.let { loading ->
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }
    }

}