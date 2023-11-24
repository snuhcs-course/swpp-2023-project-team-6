package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.NoRippleInteractionSource
import com.example.speechbuddy.compose.utils.TopAppBarUi
import com.example.speechbuddy.viewmodel.AccountSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSettings(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    bottomPaddingValues: PaddingValues,
    viewModel: AccountSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isGuestMode = uiState.user == null // user == null 이면 게스트 모드인 것으로 간주

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(topBar = {
            TopAppBarUi(title = stringResource(id = R.string.settings))
        }) { topPaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = topPaddingValues.calculateTopPadding(),
                        bottom = bottomPaddingValues.calculateBottomPadding()
                    )
                    .padding(24.dp)
            ) {
                SettingsTextButton(
                    text = stringResource(id = R.string.account),
                    onClick = {
                        if (isGuestMode) navController.navigate("guest")
                        else navController.navigate("account")
                    },
                )

                SettingsTextButton(text = stringResource(id = R.string.display),
                    onClick = { navController.navigate("display") })

                SettingsTextButton(text = stringResource(id = R.string.manage_symbols),
                    onClick = { navController.navigate("my_symbol") },
                )

                SettingsTextButton(
                    text = stringResource(id = R.string.backup_to_server),
                    onClick = { navController.navigate("backup") },
                    enabled = !isGuestMode
                )

                SettingsTextButton(text = stringResource(id = R.string.version_info),
                    onClick = { navController.navigate("version") })

                SettingsTextButton(text = stringResource(id = R.string.developers_info),
                    onClick = { navController.navigate("developers") })

                SettingsTextButton(text = stringResource(id = R.string.copyright_info),
                    onClick = { navController.navigate("copyright") })
            }
        }
    }
}

@Composable
fun SettingsTextButton(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        interactionSource = NoRippleInteractionSource()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}