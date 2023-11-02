package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.speechbuddy.R
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun SettingsScreen() {
    val navController = rememberNavController()
    SettingsScreenNavHost(
        navController = navController
    )
}

@Composable
private fun SettingsScreenNavHost(
    navController: NavHostController
) {
    val navigateToMain = { navController.navigate("main") }

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainSettings(
                navController = navController
            )
        }
        composable("account") {
            AccountSettings(
                onBackClick = navigateToMain,
                email = "example@example.com",
                nickname = "example",
            )
        }
        composable("backup") {
            BackupSettings(
                modifier = Modifier,
                onBackClick = navigateToMain,
                lastBackupDate = "2023.10.29."
            )
        }
        composable("display") {
            DisplaySettings(
                modifier = Modifier,
                onBackClick = navigateToMain
            )
        }
        composable("my_symbol") {
            MySymbolSettings()
        }
        composable("version") {
            VersionInfo(
                modifier = Modifier,
                onBackClick = navigateToMain,
                versionText = "1.0.0"
            )
        }
        composable("developers") {
            DevelopersInfo(
                modifier = Modifier,
                onBackClick = navigateToMain
            )
        }
    }
}

@Composable
fun MainSettings(
    navController: NavHostController
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 50.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingsTextButtonUi(text = stringResource(id = R.string.settings_account_button),
                onClick = { navController.navigate("account") })

            SettingsTextButtonUi(text = stringResource(id = R.string.settings_backup_button),
                onClick = { navController.navigate("backup") })

            SettingsTextButtonUi(text = stringResource(id = R.string.settings_display_button),
                onClick = { navController.navigate("display") })

            SettingsTextButtonUi(text = stringResource(id = R.string.settings_manage_symbol_button),
                onClick = { navController.navigate("my_symbol") })

            SettingsTextButtonUi(text = stringResource(id = R.string.settings_versioninfo_button),
                onClick = { navController.navigate("version") })

            SettingsTextButtonUi(text = stringResource(id = R.string.settings_developerinfo_button),
                onClick = { navController.navigate("developers") })
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    SpeechBuddyTheme {
        SettingsScreen()
    }
}