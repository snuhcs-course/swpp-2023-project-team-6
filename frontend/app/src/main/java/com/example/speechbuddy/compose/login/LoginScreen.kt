package com.example.speechbuddy.compose.login

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.AuthTopAppBarUi
import com.example.speechbuddy.compose.utils.ButtonLevel
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.ProgressIndicatorUi
import com.example.speechbuddy.compose.utils.TextFieldUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.ui.models.LoginErrorType
import com.example.speechbuddy.utils.Status
import com.example.speechbuddy.viewmodel.LoginViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onResetPasswordClick: () -> Unit,
    onSignupClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val isEmailError = uiState.error?.type == LoginErrorType.EMAIL
    val isPasswordError = uiState.error?.type == LoginErrorType.PASSWORD
    val isError = (isEmailError || isPasswordError) &&
            (uiState.error?.messageId != R.string.internet_error)

    Surface(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = { AuthTopAppBarUi(onBackClick = onBackClick) }
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleUi(
                    title = stringResource(id = R.string.login_text),
                    description = stringResource(id = R.string.login_explain)
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Email Text Field
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

                // Password Text Field
                TextFieldUi(
                    value = viewModel.passwordInput,
                    onValueChange = { viewModel.setPassword(it) },
                    label = { Text(stringResource(id = R.string.password_field)) },
                    supportingText = {
                        if (isPasswordError) {
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
                    // Login Button
                    ButtonUi(
                        text = stringResource(id = R.string.login_text),
                        onClick = {
                            viewModel.login()
                        },
                        isEnabled = !isError,
                        isError = isError
                    )
                    // Forgot Password Button
                    ButtonUi(
                        text = stringResource(id = R.string.forgot_password),
                        onClick = onResetPasswordClick,
                        level = ButtonLevel.SECONDARY
                    )
                }

                // Signup Button
                ButtonUi(
                    text = stringResource(id = R.string.signup),
                    onClick = onSignupClick,
                    modifier = Modifier.offset(y = 160.dp),
                )
            }
        }
    }

    uiState.loading.let{
        if (it) {
            ProgressIndicatorUi()
        }
    }
}

