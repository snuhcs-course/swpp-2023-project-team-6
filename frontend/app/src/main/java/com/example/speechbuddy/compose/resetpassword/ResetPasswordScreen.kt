package com.example.speechbuddy.compose.resetpassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.speechbuddy.ui.models.ResetPasswordErrorType
import com.example.speechbuddy.viewmodel.ResetPasswordViewModel

@Composable
fun ResetPasswordScreen(
    modifier: Modifier = Modifier,
    navigateToLogin: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isPasswordError = uiState.error?.type == ResetPasswordErrorType.PASSWORD
    val isPasswordCheckError = uiState.error?.type == ResetPasswordErrorType.PASSWORD_CHECK
    val isConnectionError = uiState.error?.type == ResetPasswordErrorType.CONNECTION
    val isError = (isPasswordError || isPasswordCheckError) && !isConnectionError

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleUi(
                title = stringResource(id = R.string.reset_password),
                description = stringResource(id = R.string.reset_password_description)
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextFieldUi(
                value = viewModel.passwordInput,
                onValueChange = { viewModel.setPassword(it) },
                label = { Text(stringResource(id = R.string.new_password)) },
                supportingText = {
                    if (isPasswordError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    } else {
                        Text(stringResource(id = R.string.password_too_short))
                    }
                },
                isError = isPasswordError,
                isValid = uiState.isValidPassword,
                isHidden = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    autoCorrect = false
                )
            )

            TextFieldUi(
                value = viewModel.passwordCheckInput,
                onValueChange = { viewModel.setPasswordCheck(it) },
                label = { Text(stringResource(id = R.string.new_password_check)) },
                supportingText = {
                    if (isPasswordCheckError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    } else if (isConnectionError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    }
                },
                isError = isError,
                isValid = uiState.isValidPassword,
                isHidden = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    autoCorrect = false
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            ButtonUi(
                text = stringResource(id = R.string.next),
                onClick = { viewModel.resetPassword(onSuccess = navigateToLogin) },
                isError = isError,
                level = ButtonLevel.PRIMARY
            )
        }
    }
}