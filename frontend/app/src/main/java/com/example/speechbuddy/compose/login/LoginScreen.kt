package com.example.speechbuddy.compose.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonLevel
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TextFieldUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.ui.models.LoginErrorType
import com.example.speechbuddy.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onResetPasswordClick: () -> Unit,
    onSignupClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val isEmailError = uiState.error?.type == LoginErrorType.EMAIL
    val isPasswordError = uiState.error?.type == LoginErrorType.PASSWORD
    val isConnectionError = uiState.error?.type == LoginErrorType.CONNECTION
    val isError = (isEmailError || isPasswordError) && !isConnectionError

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleUi(
                title = stringResource(id = R.string.login),
                description = stringResource(id = R.string.login_description)
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextFieldUi(
                value = viewModel.emailInput,
                onValueChange = { viewModel.setEmail(it) },
                label = { Text(stringResource(id = R.string.email)) },
                supportingText = {
                    if (isEmailError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    }
                },
                isError = isError,
                isValid = uiState.isValidEmail
            )

            TextFieldUi(
                value = viewModel.passwordInput,
                onValueChange = { viewModel.setPassword(it) },
                label = { Text(stringResource(id = R.string.password)) },
                supportingText = {
                    if (isPasswordError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    } else if (isConnectionError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    }
                },
                isError = isError,
                isValid = uiState.isValidPassword,
                isHidden = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ButtonUi(
                    text = stringResource(id = R.string.login),
                    onClick = {
                        viewModel.login()
                    },
                    isEnabled = !isError,
                    isError = isError
                )
                ButtonUi(
                    text = stringResource(id = R.string.forgot_password),
                    onClick = onResetPasswordClick,
                    isError = isError,
                    level = ButtonLevel.SECONDARY
                )
            }

            ButtonUi(
                text = stringResource(id = R.string.signup),
                onClick = onSignupClick,
                modifier = Modifier.offset(y = 110.dp),
            )
        }
    }
}