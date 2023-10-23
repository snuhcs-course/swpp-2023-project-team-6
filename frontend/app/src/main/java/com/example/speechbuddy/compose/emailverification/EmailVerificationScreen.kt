package com.example.speechbuddy.compose.emailverification

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonLevel
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TextFieldUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.compose.utils.TopAppBarUi
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.ui.models.EmailVerificationErrorType
import com.example.speechbuddy.viewmodel.EmailVerificationViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailVerificationScreen(
    source: String?,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    viewModel: EmailVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEmailError = uiState.error?.type == EmailVerificationErrorType.EMAIL
    val isVerifyNumberError = uiState.error?.type == EmailVerificationErrorType.VERIFY_NUMBER
    val isError = isEmailError || isVerifyNumberError

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = { TopAppBarUi(onBackClick = onBackClick) }
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
                        "reset_password" -> stringResource(id = R.string.reset_password_title)
                        "signup" -> stringResource(id = R.string.verify_email_field)
                        else -> stringResource(id = R.string.verify_email_field)
                    },
                    description = when (source) {
                        "reset_password" -> stringResource(id = R.string.reset_password_subtitle1)
                        "signup" -> stringResource(id = R.string.verify_email_explain)
                        else -> stringResource(id = R.string.verify_email_explain_default)
                    }
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Email Text Field
                TextFieldUi(
                    value = viewModel.emailInput,
                    onValueChange = { viewModel.setEmail(it) },
                    label = { Text(text = stringResource(id = R.string.email_field)) },
                    supportingButton = {
                        ButtonUi(
                            text = stringResource(id = R.string.send_validation_number),
                            onClick = {
                                when (source) {
                                    "reset_password" -> viewModel.verifySendPW()
                                    "signup" -> viewModel.verifySendSignup()
                                    else -> viewModel.verifySendSignup()
                                }
                            },
                            // 성공적으로 보내지면 TextFieldUi의 isEnalbed가 false가 돼야함
                            level = ButtonLevel.TERTIARY
                        )
                    },
                    supportingText = {
                        if (isEmailError) {
                            Text(stringResource(id = uiState.error!!.messageId))
                        }
                    },
                    isError = isError,
                    isValid = uiState.isValidEmail
                )

                // Verify number(code) Text Field
                TextFieldUi(
                    value = viewModel.verifyNumberInput,
                    onValueChange = { viewModel.setVerifyNumber(it) },
                    label = { Text(text = stringResource(id = R.string.validation_number)) },
                    supportingText = {
                        if (isVerifyNumberError) {
                            Text(stringResource(id = uiState.error!!.messageId))
                        }
                    },
                    isError = isError,
                    isValid = uiState.isValidVerifyNumber,
                )

                Spacer(modifier = Modifier.height(15.dp))

                ButtonUi(
                    text = stringResource(id = R.string.next),
                    onClick = {
                        when (source) {
                            "reset_password" -> viewModel.verifyAcceptPW()
                            "signup" -> viewModel.verifyAcceptSignup()
                            else -> viewModel.verifyAcceptSignup()
                        }
                    },
                )
            }
        }
    }
}


@Preview
@Composable
fun EmailVerificationScreenPreview() {
    SpeechBuddyTheme {
        EmailVerificationScreen(
            source = "reset_password",
            onBackClick = {},
            onNextClick = {}
        )
    }
}