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
fun SettingsScreen(

) {
    val navController = rememberNavController()
    SettingsScreenNavHost(
        navController = navController
    )
}

@Composable
private fun SettingsScreenNavHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = "settings_main") {
        composable("settings_main") {
            SettingsMainScreen(
                onUserSettingsClick = { navController.navigate("settings_usersettings") },
                onBackupClick = { navController.navigate("settings_backup") },
                onDisplayClick = { navController.navigate("settings_display") },
                onManageSymbolClick = { navController.navigate("settings_managesymbol") },
                onVersionInfoClick = { navController.navigate("settings_versioninfo") },
                onDeveloperInfoClick = { navController.navigate("settings_developerinfo") }
            )
        }
        composable("settings_usersettings") {
            UserSettingsScreen(
                onBackClick = { navController.navigate("settings_main") },
                email = "example@example.com",
                nickname = "example",
            )
        }
        composable("settings_backup") {
            BackupScreen(
                modifier = Modifier,
                onBackClick = { navController.navigate("settings_main") },
                lastBackupDate = "2023.10.29.",
            )
        }
        composable("settings_display") {
            DisplayScreen(
                modifier = Modifier,
                onBackClick = { navController.navigate("settings_main") },
            )
        }
        composable("settings_managesymbol") {
            ManageSymbolScreen()
        }
        composable("settings_versioninfo") {
            VersionInfoScreen(
                modifier = Modifier,
                onBackClick = { navController.navigate("settings_main") },
                versionText = "1.0.0"
            )
        }
        composable("settings_developerinfo") {
            DeveloperInfoScreen(
                modifier = Modifier,
                onBackClick = { navController.navigate("settings_main") }
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