package com.example.speechbuddy.compose.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                onBackClick = navigateToMain
            )
        }
        composable("backup") {
            BackupSettings(
                onBackClick = navigateToMain
            )
        }
        composable("display") {
            DisplaySettings(
                onBackClick = navigateToMain
            )
        }
        composable("my_symbol") {
            MySymbolSettings(
                modifier = Modifier,
                onBackClick = navigateToMain
            )
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

@Preview
@Composable
private fun SettingsScreenPreview() {
    SpeechBuddyTheme {
        SettingsScreen()
    }
}