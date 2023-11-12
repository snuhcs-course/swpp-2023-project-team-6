package com.example.speechbuddy.compose.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TextFieldUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.ui.models.SignupErrorType
import com.example.speechbuddy.viewmodel.SignupViewModel

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    email: String,
    navigateToLogin: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isNicknameError = uiState.error?.type == SignupErrorType.NICKNAME
    val isPasswordError = uiState.error?.type == SignupErrorType.PASSWORD
    val isPasswordCheckError = uiState.error?.type == SignupErrorType.PASSWORD_CHECK
    val isConnectionError = uiState.error?.type == SignupErrorType.CONNECTION
    val isError = (isNicknameError || isPasswordError || isPasswordCheckError) && !isConnectionError

    LaunchedEffect(Unit) {
        viewModel.setEmail(email)
    }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleUi(
                title = stringResource(id = R.string.signup),
                description = stringResource(id = R.string.signup_description)
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextFieldUi(
                value = email,
                onValueChange = {},
                label = { Text(text = stringResource(id = R.string.email)) },
                isEnabled = false
            )

            TextFieldUi(
                value = viewModel.nicknameInput,
                onValueChange = { viewModel.setNickname(it) },
                label = { Text(text = stringResource(id = R.string.nickname)) },
                supportingText = {
                    if (isNicknameError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    }
                },
                isError = isNicknameError,
                isValid = uiState.isValidNickname
            )

            TextFieldUi(
                value = viewModel.passwordInput,
                onValueChange = { viewModel.setPassword(it) },
                label = { Text(text = stringResource(id = R.string.password)) },
                supportingText = {
                    if (isPasswordError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    }
                },
                isError = isPasswordError,
                isValid = uiState.isValidPassword,
                isHidden = true
            )

            TextFieldUi(
                value = viewModel.passwordCheckInput,
                onValueChange = { viewModel.setPasswordCheck(it) },
                label = { Text(text = stringResource(id = R.string.password_check)) },
                supportingText = {
                    if (isPasswordCheckError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    } else if (isConnectionError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    }
                },
                isError = isPasswordError || isPasswordCheckError,
                isValid = uiState.isValidPassword,
                isHidden = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            ButtonUi(
                text = stringResource(id = R.string.signup),
                isError = isError,
                onClick = {
                    viewModel.signup(onSuccess = navigateToLogin)
                }
            )
        }
    }
}