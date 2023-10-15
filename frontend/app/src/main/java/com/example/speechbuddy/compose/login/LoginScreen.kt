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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonLevel
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TextFieldUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.compose.utils.TopAppBarUi
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.viewmodels.LoginViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onResetPasswordClick: () -> Unit,
    onSignupClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
    ) {

        Scaffold(
            topBar = {
                TopAppBarUi(
                    onBackClick = onBackClick
                )
            }
        ) {
            val viewModel = remember { LoginViewModel() }

            Column(
                modifier = Modifier
                    .padding(horizontal = 25.dp, vertical = 35.dp)
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
                    value = viewModel.getEmail(),
                    onValueChange = { viewModel.validateEmail(it) },
                    label = { Text(stringResource(id = R.string.email_field)) },
                    supportingText = {
                        if (viewModel.getEmailError()) {
                            Text(stringResource(id = R.string.false_email))
                        }
                    },
                    isError = viewModel.getEmailError(),
                    isValid = false
                )

                // Password Text Field
                TextFieldUi(
                    label = { Text(stringResource(id = R.string.password_field)) },
                    value = viewModel.getPassword(),
                    onValueChange = { viewModel.validatePassword(it) },
                    supportingText = {
                        if (viewModel.getPasswordError()) {
                            Text(stringResource(id = R.string.false_password))
                        }
                    },
                    isError = viewModel.getPasswordError(),
                    isValid = false,
                    isHidden = true
                )

                // Login Button
                ButtonUi(
                    text = stringResource(id = R.string.login_text),
                    onClick = { onLoginClick() },
                    isError = false,
                    isEnabled = true,
                    level = ButtonLevel.PRIMARY
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Forgot Password Button
                ButtonUi(
                    text = stringResource(id = R.string.forgot_passowrd),
                    onClick = onResetPasswordClick,
                    isEnabled = true,
                    isError = false,
                    level = ButtonLevel.SECONDARY
                )
                // Signup Button
                ButtonUi(
                    text = stringResource(id = R.string.signup),
                    onClick = onSignupClick,
                    modifier = Modifier.offset(y = 160.dp),
                    isError = false
                )
            }
        }
    }
}


@Preview
@Composable
private fun LoginScreenPreview() {
    SpeechBuddyTheme {
        LoginScreen(
            onBackClick = {},
            onSignupClick = {},
            onResetPasswordClick = {},
            onLoginClick = {})
    }
}

