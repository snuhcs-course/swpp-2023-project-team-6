package com.example.speechbuddy.compose.resetpassword

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonLevel
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TextFieldUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.compose.utils.AuthTopAppBarUi
import com.example.speechbuddy.ui.models.ResetPasswordErrorType
import com.example.speechbuddy.viewmodel.ResetPasswordViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    navController: NavHostController,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isPasswordError = uiState.error?.type == ResetPasswordErrorType.PASSWORD
    val isPasswordCheckError = uiState.error?.type == ResetPasswordErrorType.PASSWORD_CHECK
    val isError = isPasswordError || isPasswordCheckError

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                AuthTopAppBarUi(
                    onBackClick = onBackClick
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleUi(
                    title = stringResource(id = R.string.reset_password_title),
                    description = stringResource(id = R.string.reset_password_subtitle2)
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Password Text Field
                TextFieldUi(
                    value = viewModel.passwordInput,
                    onValueChange = { viewModel.setPassword(it) },
                    label = { Text(stringResource(id = R.string.new_password_field)) },
                    supportingText = {
                        if (isPasswordError) {
                            Text(stringResource(id = uiState.error!!.messageId))
                        }
                    },
                    isError = isError,
                    isValid = uiState.isValidPassword,
                    isHidden = true
                )

                // Password Check Text Field
                TextFieldUi(
                    value = viewModel.passwordCheckInput,
                    onValueChange = { viewModel.setPasswordCheck(it) },
                    label = { Text(stringResource(id = R.string.new_password_check_field)) },
                    supportingText = {
                        if (isPasswordCheckError) {
                            Text(stringResource(id = uiState.error!!.messageId))
                        }
                    },
                    isError = isError,
                    isValid = uiState.isValidPassword,
                    isHidden = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Set password Button
                ButtonUi(
                    text = stringResource(id = R.string.reset_password_next),
                    onClick = { viewModel.resetPassword(navController) },
                    isError = isError,
                    level = ButtonLevel.PRIMARY
                )
            }
        }
    }
}