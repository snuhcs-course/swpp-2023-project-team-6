package com.example.speechbuddy.compose.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.speechbuddy.viewmodel.GuideScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    bottomPaddingValues: PaddingValues,
    guideScreenViewModel: GuideScreenViewModel
) {
    val navController = rememberNavController()
    SettingsScreenNavHost(
        navController = navController,
        bottomPaddingValues = bottomPaddingValues,
        guideScreenViewModel = guideScreenViewModel
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SettingsScreenNavHost(
    navController: NavHostController,
    bottomPaddingValues: PaddingValues,
    guideScreenViewModel: GuideScreenViewModel
) {
    val navigateToMain = { navController.navigate("main") }

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainSettings(
                navController = navController,
                bottomPaddingValues = bottomPaddingValues,
                guideScreenViewModel = guideScreenViewModel
            )
        }
        composable("account") {
            AccountSettings(
                onBackClick = navigateToMain,
                bottomPaddingValues = bottomPaddingValues,
                guideScreenViewModel = guideScreenViewModel
            )
        }
        composable("guest") {
            GuestSettings(
                onBackClick = navigateToMain,
                bottomPaddingValues = bottomPaddingValues,
                guideScreenViewModel = guideScreenViewModel
            )
        }
        composable("display") {
            DisplaySettings(
                onBackClick = navigateToMain,
                bottomPaddingValues = bottomPaddingValues,
                guideScreenViewModel = guideScreenViewModel
            )
        }
        composable("my_symbol") {
            MySymbolSettings(
                bottomPaddingValues = bottomPaddingValues,
                guideScreenViewModel = guideScreenViewModel
            )
        }
        composable("backup") {
            BackupSettings(
                onBackClick = navigateToMain,
                bottomPaddingValues = bottomPaddingValues,
                guideScreenViewModel = guideScreenViewModel
            )
        }
        composable("version") {
            VersionInfo(
                onBackClick = navigateToMain,
                bottomPaddingValues = bottomPaddingValues,
                guideScreenViewModel = guideScreenViewModel
            )
        }
        composable("developers") {
            DevelopersInfo(
                onBackClick = navigateToMain,
                bottomPaddingValues = bottomPaddingValues,
                guideScreenViewModel = guideScreenViewModel
            )
        }
        composable("copyright") {
            Copyright(
                onBackClick = navigateToMain,
                bottomPaddingValues = bottomPaddingValues,
                guideScreenViewModel = guideScreenViewModel
            )
        }
    }
}