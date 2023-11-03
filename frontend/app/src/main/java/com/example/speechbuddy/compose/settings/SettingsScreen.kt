package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SettingsScreen(
    bottomPaddingValues: PaddingValues
) {
    val navController = rememberNavController()
    SettingsScreenNavHost(
        navController = navController,
        bottomPaddingValues = bottomPaddingValues
    )
}

@Composable
private fun SettingsScreenNavHost(
    navController: NavHostController,
    bottomPaddingValues: PaddingValues
) {
    val navigateToMain = { navController.navigate("main") }

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainSettings(
                navController = navController,
                bottomPaddingValues = bottomPaddingValues
            )
        }
        composable("account") {
            AccountSettings(
                onBackClick = navigateToMain,
                bottomPaddingValues = bottomPaddingValues
            )
        }
        composable("backup") {
            BackupSettings(
                onBackClick = navigateToMain,
                bottomPaddingValues = bottomPaddingValues
            )
        }
        composable("display") {
            DisplaySettings(
                onBackClick = navigateToMain,
                bottomPaddingValues = bottomPaddingValues
            )
        }
        composable("my_symbol") {
            MySymbolSettings(
                onBackClick = navigateToMain,
                bottomPaddingValues = bottomPaddingValues
            )
        }
        composable("version") {
            VersionInfo(
                onBackClick = navigateToMain,
                bottomPaddingValues = bottomPaddingValues
            )
        }
        composable("developers") {
            DevelopersInfo(
                onBackClick = navigateToMain,
                bottomPaddingValues = bottomPaddingValues
            )
        }
    }
}