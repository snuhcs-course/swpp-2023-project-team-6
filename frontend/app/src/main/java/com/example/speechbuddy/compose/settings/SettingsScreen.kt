package com.example.speechbuddy.compose.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    paddingValues: PaddingValues
) {
    val navController = rememberNavController()
    SettingsScreenNavHost(
        navController = navController,
        paddingValues = paddingValues
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SettingsScreenNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainSettings(
                navController = navController,
                paddingValues = paddingValues
            )
        }
        composable("account") {
            AccountSettings(
                paddingValues = paddingValues
            )
        }
        composable("guest") {
            GuestSettings(
                paddingValues = paddingValues
            )
        }
        composable("display") {
            DisplaySettings(
                paddingValues = paddingValues
            )
        }
        composable("my_symbol") {
            MySymbolSettings(
                paddingValues = paddingValues
            )
        }
        composable("backup") {
            BackupSettings(
                paddingValues = paddingValues
            )
        }
        composable("version") {
            VersionInfo(
                paddingValues = paddingValues
            )
        }
        composable("developers") {
            DevelopersInfo(
                paddingValues = paddingValues
            )
        }

        composable("copyright") {
            Copyright(
                paddingValues = paddingValues
            )
        }
    }
}