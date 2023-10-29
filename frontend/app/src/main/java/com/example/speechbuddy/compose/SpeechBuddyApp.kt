package com.example.speechbuddy.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.addsymbol.AddSymbolScreen
import com.example.speechbuddy.compose.home.BottomNavigationBar
import com.example.speechbuddy.compose.landing.LandingScreen
import com.example.speechbuddy.compose.login.LoginScreen
import com.example.speechbuddy.compose.resetpassword.EmailVerificationScreen
import com.example.speechbuddy.compose.resetpassword.ResetPasswordScreen
import com.example.speechbuddy.compose.settings.AccountScreen
import com.example.speechbuddy.compose.settings.BackupScreen
import com.example.speechbuddy.compose.settings.DeveloperInfoScreen
import com.example.speechbuddy.compose.settings.DisplayScreen
import com.example.speechbuddy.compose.settings.ManageSymbolScreen
import com.example.speechbuddy.compose.settings.SettingsScreen
import com.example.speechbuddy.compose.settings.VersionInfoScreen
import com.example.speechbuddy.compose.signup.SignupScreen
import com.example.speechbuddy.compose.symbolselection.SymbolSelectionScreen
import com.example.speechbuddy.compose.tts.TextToSpeechScreen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeechBuddyApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = listOf(
                    BottomNavItem(
                        "SymbolSelection",
                        "symbolselection",
                        R.drawable.outline_touch_app_24
                    ),
                    BottomNavItem("TTS", "tts", R.drawable.outline_volume_up_24),
                    BottomNavItem("AddSymbol", "addsymbol", R.drawable.outline_add_a_photo_24),
                    BottomNavItem("Settings", "settings", R.drawable.outline_settings_24)
                ),
                navController = navController,
                modifier = Modifier
                    .padding(horizontal = 0.dp, vertical = 10.dp)
                    .height(100.dp),
                onItemClick = {
                    navController.navigate(it.route)
                }
            )
        }
    ) {
        SpeechBuddyNavHost(
            navController = navController
        )
    }


//    SpeechBuddyNavHost(
//        navController = navController
//    )
}

@Composable
fun SpeechBuddyNavHost(
    navController: NavHostController
) {
    // val activity = (LocalContext.current as Activity)
    NavHost(navController = navController, startDestination = "symbolselection") {
        composable("landing") {
            LandingScreen(
                onLoginClick = {
                    navController.navigate("login")
                }
            )
        }
        composable("login") {
            LoginScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onResetPasswordClick = {
                    navController.navigate("email_verification/password")
                },
                onSignupClick = {
                    navController.navigate("signup")
                }
            )
        }
        composable("signup") {
            SignupScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onSignupClick = {},
                email = ""
            )
        }

//        composable("resetpasswordcheck") {
//            ResetPasswordCheck(
//                onLoginClick = {
//                    navController.navigate("login")

        composable("email_verification/password") {
            EmailVerificationScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onSubmitClick = {
                },
                onNextClick = {
                    navController.navigate("reset_password")
                }
            )
        }

        composable("reset_password") {
            ResetPasswordScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onNextClick = {}

            )
        }
        composable("symbolselection") {
            SymbolSelectionScreen()
        }
        composable("tts") {
            TextToSpeechScreen()
        }
        composable("addsymbol") {
            AddSymbolScreen()
        }
        composable("settings") {
            SettingsScreen(
                onAccountClick = { navController.navigate("settings_account") },
                onBackupClick = { navController.navigate("settings_backup") },
                onDisplayClick = { navController.navigate("settings_display") },
                onManageSymbolClick = { navController.navigate("settings_managesymbol") },
                onVersionInfoClick = { navController.navigate("settings_versioninfo") },
                onDeveloperInfoClick = { navController.navigate("settings_developerinfo") }
            )
        }
        composable("settings_account") {
            var showLogoutDialog by remember { mutableStateOf(false) }
            var showWithdrawalDialog by remember { mutableStateOf(false) }
            var showSecondWithdrawalDialog by remember { mutableStateOf(false) }
            AccountScreen(
                showLogoutDialog = showLogoutDialog,
                onShowLogoutDialog = { showLogoutDialog = true },
                onHideLogoutDialog = { showLogoutDialog = false },
                showWithdrawalDialog = showWithdrawalDialog,
                onShowWithdrawalDialog = { showWithdrawalDialog = true },
                onHideWithdrawalDialog = { showWithdrawalDialog = false },
                showSecondWithdrawalDialog = showSecondWithdrawalDialog,
                onShowSecondWithdrawalDialog = { showSecondWithdrawalDialog = true },
                onHideSecondWithdrawalDialog = { showSecondWithdrawalDialog = false },
                onBackClick = { navController.navigate("settings") },
                email = "",
                nickname = ""
            )
        }
        composable("settings_backup") {
            BackupScreen(
                modifier = Modifier,
                onBackClick = { navController.navigate("settings") },
                onBackupClick = {},
                lastBackupDate = "blabla",
                automaticBackupChecked = false,
                onAutomaticBackupCheckedChange = {}
            )
        }
        composable("settings_display") {
            var darkModeChecked by remember { mutableStateOf(false) }
            DisplayScreen(
                modifier = Modifier,
                onBackClick = { navController.navigate("settings") },
                darkModeChecked = darkModeChecked,
                onDarkModeCheckedChange = { darkModeChecked = true }
            )
        }
        composable("settings_managesymbol") {
            ManageSymbolScreen()
        }
        composable("settings_versioninfo") {
            VersionInfoScreen(
                modifier = Modifier,
                onBackClick = { navController.navigate("settings") },
                versionText = "blabla"
            )
        }
        composable("settings_developerinfo") {
            DeveloperInfoScreen(
                modifier = Modifier,
                onBackClick = { navController.navigate("settings") }
            )
        }
    }
}