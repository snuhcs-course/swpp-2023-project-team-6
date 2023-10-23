package com.example.speechbuddy.compose.login

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.AlertDialogUi
import com.example.speechbuddy.compose.utils.ButtonLevel
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TextFieldUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.compose.utils.TopAppBarUi
import com.example.speechbuddy.ui.models.LoginErrorType
import com.example.speechbuddy.utils.Resource
import com.example.speechbuddy.utils.Status
import com.example.speechbuddy.viewmodel.LoginViewModel
import com.example.speechbuddy.viewmodel.ResultType
import kotlinx.coroutines.delay

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
    val isError = isEmailError || isPasswordError
    val response by viewModel.loginResult.observeAsState(null)
    val openAlertDialog = remember { mutableStateOf(false) }
    var alertMessage : String = ""

    Surface(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = { TopAppBarUi(onBackClick = onBackClick) }
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
                    label = { Text(stringResource(id = R.string.email_field)) },
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

                // Login Button
                ButtonUi(
                    text = stringResource(id = R.string.login_text),
                    onClick = {
                        viewModel.login()
                        when (response){
                            is ResultType.Success -> {
                                Log.d("test", "success: " + response.toString())

                                //move to next screen
                                // TODO
                            }
                            is ResultType.Error -> {
                                openAlertDialog.value = true
                                alertMessage = response.toString()
                                Log.d("test", "error: " + response.toString())
                            }

                            else -> {
                                Log.d("test", "else: " + response.toString())
                                Thread.sleep(2000)
                            }
                        }
                    },
                    isEnabled = !isError,
                    isError = isError
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Forgot Password Button
                ButtonUi(
                    text = stringResource(id = R.string.forgot_password),
                    onClick = onResetPasswordClick,
                    isError = isError,
                    level = ButtonLevel.SECONDARY
                )

                // Signup Button
                ButtonUi(
                    text = stringResource(id = R.string.signup),
                    onClick = onSignupClick,
                    modifier = Modifier.offset(y = 160.dp),
                )
            }
        }
    }

    // Pop up alert dialog
    when {
        openAlertDialog.value -> {
            // For some reason first few result?.get("msg") returns null.
            // Possible do to async?
            // Workaround by comparing to null
//            val message: String
//            if (result?.get("msg") == null) {
//                message = ""
//            } else {
//                message = result?.get("msg").toString()
//            }
            Log.d("test", "run alert")

            AlertDialogUi(
                onConfirmation = {
                    openAlertDialog.value = false
                },
                dialogTitle = stringResource(id = R.string.alert_dialog_login_error),
                dialogText = alertMessage,
            )
        }
    }
}

