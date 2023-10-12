package com.example.speechbuddy.compose.signup

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.LoginTopAppBarUi
import com.example.speechbuddy.ui.SpeechBuddyTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.LoginPageExplanationUi
import com.example.speechbuddy.ui.md_theme_light_primary
import com.example.speechbuddy.ui.md_theme_light_error

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onSignupClick: () -> Unit,
    onPreviousClick: () -> Unit,
    email: String
) {
    SpeechBuddyTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ){
            Scaffold(
                topBar = { LoginTopAppBarUi(onPreviousClick) }
            ){
                var nickname by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var passwordCheck by remember {mutableStateOf("")}

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ){
                    LoginPageExplanationUi(
                        titleText=R.string.signup_text,
                        explainText=R.string.signup_explain
                    )

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                            .height(45.dp),
                        value = email,
                        onValueChange = {},
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = md_theme_light_primary
                        ), textStyle = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(25.dp))

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                            .height(45.dp),
                        value = nickname,
                        onValueChange = {nickname = it},
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = md_theme_light_error)
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp, vertical = 4.dp),
                        text = stringResource(id = R.string.false_nickname),
                        style = MaterialTheme.typography.bodySmall,
                        color = md_theme_light_error,
                    )

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                            .height(45.dp),
                        value = password,
                        onValueChange = {password = it},
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = md_theme_light_error)
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp, vertical = 4.dp),
                        text = stringResource(id = R.string.false_password),
                        style = MaterialTheme.typography.bodySmall,
                        color = md_theme_light_error
                    )

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                            .height(45.dp),
                        value = passwordCheck,
                        onValueChange = {passwordCheck = it},
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = md_theme_light_error)
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp, vertical = 4.dp),
                        text = stringResource(id = R.string.false_password),
                        style = MaterialTheme.typography.bodySmall,
                        color = md_theme_light_error
                    )

                    ButtonUi(text = stringResource(id=R.string.signup), onClick = { /*TODO*/ },
                        modifier=Modifier.padding(horizontal = 5.dp))
                }

            }
        }
    }
}

@Preview
@Composable
fun SignupScreenPreview(){
    SpeechBuddyTheme {
        SignupScreen(onSignupClick = { /*TODO*/ }, onPreviousClick = { /*TODO*/ }, email = "asdf")
    }
}