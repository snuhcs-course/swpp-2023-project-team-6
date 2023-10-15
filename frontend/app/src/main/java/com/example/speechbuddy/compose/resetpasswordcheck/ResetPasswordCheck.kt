package com.example.speechbuddy.compose.resetpasswordcheck

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
fun ResetPasswordCheck(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onNextClick: () -> Unit
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
            val email = remember { mutableStateOf("") }
            val validationNumber = remember { mutableStateOf("") }
            
            Column(
                modifier = Modifier
                    .padding(horizontal = 25.dp, vertical = 35.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,                
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleUi(
                    title = stringResource(id = R.string.reset_passoword_title),
                    description = stringResource(id = R.string.reset_password_subtitle1)
                )

                Spacer(modifier = Modifier.height(15.dp))
                // Email Text Field
                TextFieldUi(
                    label = { Text(stringResource(id = R.string.email_field)) },
                    value = email.value,
                    onValueChange = { email.value = it },
                    supportingText = { Text(stringResource(id = R.string.false_email)) },
                    supportingButton = {
                        ButtonUi(
                            text = stringResource(id = R.string.reset_password_send_validationNumber),
                            onClick = onSubmitClick,
                            isError = false,
                            level = ButtonLevel.TERTIARY
                        )
                    },
                    isError = false,
                    isValid = false,
                    isHidden = false,
                )

                // Validation number Text Field
                TextFieldUi(
                    label = { Text(stringResource(id = R.string.validation_number_field)) },
                    value = validationNumber.value,
                    onValueChange = { validationNumber.value = it },
                    supportingText = { Text(stringResource(id = R.string.reset_password_false_validation_number)) },
                    isError = false,
                    isValid = false,
                    isHidden = false,
                )

                // Next Button
                ButtonUi(
                    text = stringResource(id = R.string.reset_password_next),
                    onClick = { onNextClick() },
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
        ResetPasswordCheck(
            onNextClick = {},
            onBackClick =  {},
            onSubmitClick = {}
        )
    }
}

