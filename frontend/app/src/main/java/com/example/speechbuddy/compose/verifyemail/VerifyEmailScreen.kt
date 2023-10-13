package com.example.speechbuddy.compose.verifyemail

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun VerifyEmailScreen(onPreviousClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(topBar = { TopAppBarUi(onBackClick = onPreviousClick) }) {
            var email by remember { mutableStateOf("") }
            var verifyNumber by remember { mutableStateOf("") }
            VerifyEmailColumn(email, verifyNumber)
        }
    }
}

@Preview
@Composable
fun VerifyEmailScreenPreview() {
    SpeechBuddyTheme {
        VerifyEmailScreen(onPreviousClick = { /*TODO*/ })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyEmailColumn(
    email: String,
    verifyNumber: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TitleUi(
            title = stringResource(id = R.string.verify_email_field),
            description = stringResource(id = R.string.verify_email_explain)
        )
        Spacer(modifier = Modifier.height(15.dp))

        TextFieldUi(value = email,
            onValueChange = {},
            label = { Text(text = stringResource(id = R.string.email_field)) },
            supportingButton = {
                ButtonUi(
                    text = stringResource(id = R.string.send_validation_number),
                    onClick = {/* TODO */ },
                    level = ButtonLevel.TERTIARY
                )
            },
            supportingText = { Text(stringResource(id = R.string.email_field)) }
        )

        TextFieldUi(value = verifyNumber,
            onValueChange = {},
            label = { Text(text = stringResource(id = R.string.validation_number)) },
            supportingText = { Text(text = stringResource(id = R.string.verify_email_field)) }
        )

        ButtonUi(
            text = stringResource(id = R.string.next), onClick = { /*TODO*/ },
        )
    }
}



