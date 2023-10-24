package com.example.speechbuddy.compose.signup

import android.annotation.SuppressLint
import com.example.speechbuddy.compose.utils.AlertDialogUi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TextFieldUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.compose.utils.TopAppBarUi
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.ui.models.SignupErrorType
import com.example.speechbuddy.utils.Resource
import com.example.speechbuddy.utils.Status
import com.example.speechbuddy.viewmodel.SignupViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSignupClick: () -> Unit,
    email: String,
    viewModel: SignupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isNicknameError = uiState.error?.type == SignupErrorType.NICKNAME
    val isPasswordError = uiState.error?.type == SignupErrorType.PASSWORD
    val isPasswordCheckError = uiState.error?.type == SignupErrorType.PASSWORDCHECK


    Surface(modifier = modifier.fillMaxSize()) {
        Scaffold(topBar = { TopAppBarUi(onBackClick = onBackClick) }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleUi(
                    title = stringResource(id = R.string.signup_text),
                    description = stringResource(id = R.string.signup_explain)
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Email Text Field
                TextFieldUi(
                    value = email,
                    onValueChange = {},
                    label = { Text(text = stringResource(id = R.string.email_field)) },
                    isEnabled = false
                )

                // Nickname Text Field
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

                // Password Text Field
                TextFieldUi(
                    value = viewModel.passwordInput,
                    onValueChange = { viewModel.setPassword(it) },
                    label = { Text(text = stringResource(id = R.string.password_field)) },
                    supportingText = {
                        if (isPasswordError) {
                            Text(stringResource(id = uiState.error!!.messageId))
                        }
                    },
                    isError = isPasswordError,
                    isValid = uiState.isValidPassword,
                    isHidden = true

                )

                // Password Check Text Field
                TextFieldUi(
                    value = viewModel.passwordCheckInput,
                    onValueChange = { viewModel.setPasswordCheck(it) },
                    label = { Text(text = stringResource(id = R.string.password_check_field)) },
                    supportingText = {
                        if (isPasswordCheckError) {
                            Text(stringResource(id = uiState.error!!.messageId))
                        }
                    },
                    isError = isPasswordCheckError,
                    isValid = uiState.isValidPassword,
                    isHidden = true
                )

                Spacer(modifier = Modifier.height(15.dp))

                ButtonUi(
                    text = stringResource(id = R.string.signup),
                    onClick = {
                        viewModel.signUp()
                    },
                )
            }
        }
    }
}
