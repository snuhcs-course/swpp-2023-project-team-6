package com.example.speechbuddy.compose.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.ui.SpeechBuddyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPageExplanationUi(
    titleText: Int,
    explainText: Int
){
    Column(){
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = titleText),
            style = MaterialTheme.typography.displayMedium,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = explainText),
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(15.dp))
    }

}

@Preview
@Composable
fun LoginPageExplanationUiPreview(){
    SpeechBuddyTheme {
        LoginPageExplanationUi(
            titleText=R.string.signup_text,
            explainText=R.string.signup_explain
        )
    }
}