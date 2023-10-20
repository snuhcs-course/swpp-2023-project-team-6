package com.example.speechbuddy.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.example.speechbuddy.compose.resetpassword.ResetPassWordScreen
import com.example.speechbuddy.compose.resetpasswordcheck.ResetPasswordCheck
import com.example.speechbuddy.compose.settings.SettingsScreen
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
                        "TalkWithSymbol",
                        "talkwithsymbol",
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
    NavHost(navController = navController, startDestination = "talkwithsymbol") {
        composable("landing") {
            LandingScreen(
                onLoginClick = {
                    navController.navigate("login")
                }
            )
        }
        composable("login") {
            LoginScreen(
                onSignupClick = {
                    navController.navigate("signup")
                },
                onLandingClick = {
                    navController.navigate("landing")
                },
                onResetPasswordClick = {
                    navController.navigate("resetpasswordcheck")
                }
            )
        }
        composable("signup") {
            //SignupScreen()
        }
        composable("resetpasswordcheck") {
            ResetPasswordCheck(
                onLoginClick = {
                    navController.navigate("login")
                },
                onNextClick = {
                    navController.navigate("resetpassword")
                }
            )
        }
        composable("resetpassword") {
            ResetPassWordScreen(
                onResetClick = {
                    navController.navigate("resetpasswordcheck")
                }
            )
        }
        composable("talkwithsymbol") {
            SymbolSelectionScreen()
        }
        composable("tts") {
            TextToSpeechScreen()
        }
        composable("addsymbol") {
            AddSymbolScreen()
        }
        composable("settings") {
            SettingsScreen()
        }
    }
}