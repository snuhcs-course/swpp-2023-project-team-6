package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.TopAppBarUi
import com.example.speechbuddy.compose.utils.NoRippleInteractionSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSettings(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    bottomPaddingValues: PaddingValues
) {
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
                SettingsTextButton(text = stringResource(id = R.string.account),
                    onClick = { navController.navigate("account") })

                SettingsTextButton(text = stringResource(id = R.string.backup_to_server),
                    onClick = { navController.navigate("backup") })

                SettingsTextButton(text = stringResource(id = R.string.display),
                    onClick = { navController.navigate("display") })

                SettingsTextButton(text = stringResource(id = R.string.manage_symbols),
                    onClick = { navController.navigate("my_symbol") })

                SettingsTextButton(text = stringResource(id = R.string.version_info),
                    onClick = { navController.navigate("version") })

                SettingsTextButton(text = stringResource(id = R.string.developers_info),
                    onClick = { navController.navigate("developers") })
            }
        }
    }
}

@Composable
fun SettingsTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        interactionSource = NoRippleInteractionSource()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}