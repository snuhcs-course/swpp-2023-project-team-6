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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonLevel
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TextFieldUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.compose.utils.TopAppBarUi
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.viewmodels.VerifyEmailViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyEmailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onNextClick: () -> Unit,
    verifyEmailViewModel: VerifyEmailViewModel = viewModel()
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(topBar = { TopAppBarUi(onBackClick = onBackClick) }) {
            VerifyEmailColumn(
                verifyEmailViewModel = verifyEmailViewModel,
                onEmailChanged = { verifyEmailViewModel.updateEmail(it) },
                onVerifyNumberChanged = { verifyEmailViewModel.updateVerifyNumber(it) }
            )
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
    verifyEmailViewModel: VerifyEmailViewModel,
    onEmailChanged: (String) -> Unit,
    onVerifyNumberChanged: (String) -> Unit
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

        TextFieldUi(value = verifyEmailViewModel.getEmail(),
            onValueChange = onEmailChanged,
            label = { Text(text = stringResource(id = R.string.email_field)) },
            supportingButton = {
                ButtonUi(
                    text = stringResource(id = R.string.send_validation_number),
                    onClick = {/* TODO */ },
                    level = ButtonLevel.TERTIARY
                )
            },
            isError = verifyEmailViewModel.warnEmail(),
            supportingText = { Text(verifyEmailViewModel.warnEmailMessage()) }
        )

        TextFieldUi(value = verifyEmailViewModel.getVerifyNumber(),
            onValueChange = onVerifyNumberChanged,
            label = { Text(text = stringResource(id = R.string.validation_number)) },
            isError = verifyEmailViewModel.warnVerifyNumber(),
            supportingText = { Text(verifyEmailViewModel.warnVerifyNumberMessage()) }
        )

        ButtonUi(
            text = stringResource(id = R.string.next), onClick = { /*TODO*/ },
        )
    }
}



