package com.example.speechbuddy.compose.login

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
import androidx.compose.runtime.mutableStateOf
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


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onSignupClick: () -> Unit,
    onLandingClick: () -> Unit,
    onResetPasswordClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                TopAppBarUi(
                    onBackClick = { onLandingClick() }
                )
            }
        ) {
            var username = remember { mutableStateOf("") }
            var password = remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .padding(horizontal = 25.dp, vertical = 35.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                TitleUi(
                    title = stringResource(id = R.string.login_text),
                    description = stringResource(id = R.string.login_explain)
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Email Text Field
                TextFieldUi(
                    label = { Text(stringResource(id = R.string.email_field)) },
                    value = username.value,
                    onValueChange = { username.value = it },
                    supportingText = { Text(stringResource(id = R.string.false_email)) },
                    isError = false,
                    isValid = false,
                    isHidden = false,
                )

                // Password Text Field
                TextFieldUi(
                    label = { Text(stringResource(id = R.string.password_field)) },
                    value = password.value,
                    onValueChange = { password.value = it },
                    supportingText = { Text(stringResource(id = R.string.false_password)) },
                    isError = false,
                    isValid = false,
                    isHidden = false,
                )

                // Login Button
                ButtonUi(
                    text = stringResource(id = R.string.login_text),
                    onClick = { /* perform login */ },
                    isError = false,
                    isEnabled = true,
                    level = ButtonLevel.PRIMARY
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Forgot Password Button
                ButtonUi(
                    text = stringResource(id = R.string.forgot_passowrd),
                    onClick = { onResetPasswordClick() },
                    isError = false,
                    isEnabled = true,
                    level = ButtonLevel.PRIMARY
                )

                Spacer(modifier = Modifier.height(145.dp))

                // Signup Button
                ButtonUi(
                    text = stringResource(id = R.string.signup),
                    onClick = { onSignupClick() },
                    isError = false,
                    isEnabled = true,
                    level = ButtonLevel.PRIMARY
                )
            }
        }
    }
}


@Preview
@Composable
private fun LoginScreenPreview() {
    SpeechBuddyTheme {
        LoginScreen(onLandingClick = {}, onSignupClick = {}, onResetPasswordClick = {})
    }
}

