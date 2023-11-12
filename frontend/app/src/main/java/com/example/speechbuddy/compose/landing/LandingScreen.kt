package com.example.speechbuddy.compose.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    onGuestClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.speechbuddy_parrot),
                contentDescription = stringResource(id = R.string.app_name),
            )
        }
        Box(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 64.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ButtonUi(
                    text = stringResource(id = R.string.start_guest_mode),
                    onClick = onGuestClick
                )
                ButtonUi(text = stringResource(id = R.string.do_login), onClick = onLoginClick)
            }
        }
    }
}

@Preview
@Composable
private fun LandingScreenPreview() {
    SpeechBuddyTheme {
        LandingScreen(onGuestClick = {}, onLoginClick = {})
    }
}