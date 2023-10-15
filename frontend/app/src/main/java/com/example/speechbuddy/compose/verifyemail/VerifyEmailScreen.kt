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
                onSubmitClick = onSubmitClick,
                onNextClick = onNextClick
            )
        }
    }
}

@Composable
fun VerifyEmailColumn(
    verifyEmailViewModel: VerifyEmailViewModel,
    onSubmitClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleUi(
            title = stringResource(id = R.string.verify_email_field),
            description = stringResource(id = R.string.verify_email_explain)
        )
        
        Spacer(modifier = Modifier.height(15.dp))

        TextFieldUi(value = verifyEmailViewModel.getEmail(),
            onValueChange = { verifyEmailViewModel.updateEmail(it) },
            label = { Text(text = stringResource(id = R.string.email_field)) },
            supportingButton = {
                ButtonUi(
                    text = stringResource(id = R.string.send_validation_number),
                    onClick = onSubmitClick,
                    level = ButtonLevel.TERTIARY
                )
            },
            supportingText = { Text(verifyEmailViewModel.warnEmailMessage()) },
            isError = verifyEmailViewModel.warnEmail()
        )

        TextFieldUi(value = verifyEmailViewModel.getVerifyNumber(),
            onValueChange = { verifyEmailViewModel.updateVerifyNumber(it) },
            label = { Text(text = stringResource(id = R.string.validation_number)) },
            supportingText = { Text(verifyEmailViewModel.warnVerifyNumberMessage()) },
            isError = verifyEmailViewModel.warnVerifyNumber()
        )

        Spacer(modifier = Modifier.height(15.dp))

        ButtonUi(
            text = stringResource(id = R.string.next), onClick = onNextClick,
        )
    }
}

@Preview
@Composable
fun VerifyEmailScreenPreview() {
    SpeechBuddyTheme {
        VerifyEmailScreen(
            onBackClick = {},
            onSubmitClick = {},
            onNextClick = {}
        )
    }
}
