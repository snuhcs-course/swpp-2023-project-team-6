package com.example.speechbuddy.compose.signup

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
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TextFieldUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.compose.utils.TopAppBarUi
import com.example.speechbuddy.ui.SpeechBuddyTheme


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSignupClick: () -> Unit,
    email: String
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(topBar = { TopAppBarUi(onBackClick = onBackClick) }) {
            var nickname by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var passwordCheck by remember { mutableStateOf("") }

            SignupColumn(
                email = email,
                nickname = nickname,
                password = password,
                passwordCheck = passwordCheck
            )
        }
    }
}

@Preview
@Composable
fun SignupScreenPreview() {
    SpeechBuddyTheme {
        SignupScreen(onSignupClick = { /*TODO*/ }, onPreviousClick = { /*TODO*/ }, email = "asdf")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupColumn(
    email: String,
    nickname: String,
    password: String,
    passwordCheck: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TitleUi(
            title = stringResource(id = R.string.signup_text),
            description = stringResource(id = R.string.signup_explain)
        )
        Spacer(modifier = Modifier.height(15.dp))

        TextFieldUi(
            value = email,
            onValueChange = {},
            label = { Text(text = stringResource(id = R.string.email_field)) },
            isEnabled = false
        )
        Spacer(modifier = Modifier.height(25.dp))

        TextFieldUi(value = nickname,
            onValueChange = {},
            label = { Text(text = stringResource(id = R.string.nickname)) },
            supportingText = { Text(text = stringResource(id = R.string.nickname)) })

        TextFieldUi(value = password,
            onValueChange = {},
            label = { Text(text = stringResource(id = R.string.password_field)) },
            supportingText = { Text(text = stringResource(id = R.string.password_field)) })

        TextFieldUi(
            value = passwordCheck,
            onValueChange = {},
            label = { Text(text = stringResource(id = R.string.password_check_field)) },
            supportingText = { Text(stringResource(id = R.string.password_field)) }
        )

        ButtonUi(
            text = stringResource(id = R.string.signup),
            onClick = { /*TODO*/ },
        )
    }
}



