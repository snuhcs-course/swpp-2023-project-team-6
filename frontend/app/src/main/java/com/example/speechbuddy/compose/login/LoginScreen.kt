package com.example.speechbuddy.compose.login

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonUi
import androidx.navigation.fragment.findNavController
import com.example.speechbuddy.viewmodels.LoginViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onSignupClick: () -> Unit,
    onLandingClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.top_app_bar_ic),
                            contentDescription = "app logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(148.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            /*TODO*/
                            onLandingClick()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Go back to landing page"
                            )
                        }
                    },
//                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
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
                Text(
                    modifier = Modifier.align(Alignment.Start),
                    text = stringResource(id = R.string.login_text),
                    style = MaterialTheme.typography.displayMedium,
                )

                Text(
                    modifier = Modifier.align(Alignment.Start),
                    text = stringResource(id = R.string.login_explain),
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = username.value,
                    onValueChange = { username.value = it },
                    label = { Text(stringResource(id = R.string.email_field)) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.error)
//                keyboardOptions = Default.copy(imeAction = ImeAction.Next),
//                keyboardActions = KeyboardActions(onNext = { /* focus next field */ })
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .height(25.dp),
                    text = stringResource(id = R.string.false_email),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text(stringResource(id = R.string.password_field)) },
                    // when keyboard action
//                keyboardOptions = Default.copy(imeAction = ImeAction.Done),
//                keyboardActions = KeyboardActions(onDone = { /* perform login */ })
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .height(40.dp),
                    text = stringResource(id = R.string.false_password),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )

                ButtonUi(
                    text = stringResource(id = R.string.login_text),
                    onClick = { /* perform login */ },
                )

                Spacer(modifier = Modifier.height(8.dp))

                ButtonUi(
                    text = stringResource(id = R.string.forgot_passowrd),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { /*forgot password*/ },
                    )

                Spacer(modifier = Modifier.height(145.dp))

                ButtonUi(
                    text = stringResource(id = R.string.signup),
                    onClick = { /* sign up */ },
                )
            }
        }
    }
}


@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen(onLandingClick = {}, onSignupClick = {})
}

