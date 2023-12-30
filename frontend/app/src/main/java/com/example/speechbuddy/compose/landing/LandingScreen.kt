package com.example.speechbuddy.compose.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.viewmodel.LoginViewModel

@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    isBackup: Boolean
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
            if (!isBackup) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ButtonUi(
                        text = stringResource(id = R.string.start_guest_mode),
                        onClick = { viewModel.enterGuestMode() }
                    )
//                    ButtonUi(text = stringResource(id = R.string.do_login), onClick = onLoginClick)
                }
            }
        }
    }

    if (isBackup) {
        Dialog(
            onDismissRequest = {}
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                ) {
                    Text(
                        text = stringResource(id = R.string.auto_backup_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .wrapContentSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(id = R.string.auto_backup_info),
                            modifier = Modifier.padding(start = 20.dp)
                        )
                    }
                }
            }
        }
    }

}