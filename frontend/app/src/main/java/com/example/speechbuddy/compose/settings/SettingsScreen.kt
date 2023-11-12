package com.example.speechbuddy.compose.settings

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SettingsScreen(
    bottomPaddingValues: PaddingValues,
    isBeingReloadedForDarkModeChange: Boolean
) {
    val navController = rememberNavController()
    SettingsScreenNavHost(
        navController = navController,
        bottomPaddingValues = bottomPaddingValues,
        isBeingReloadedForDarkModeChange = isBeingReloadedForDarkModeChange
    )
}

@Composable
private fun SettingsScreenNavHost(
    navController: NavHostController,
    bottomPaddingValues: PaddingValues,
    isBeingReloadedForDarkModeChange: Boolean
) {
    val startDestination = if (isBeingReloadedForDarkModeChange) "display" else "main"
    val navigateToMain = { navController.navigate("main") }

    NavHost(navController = navController, startDestination = startDestination) {
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