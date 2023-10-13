package com.example.speechbuddy.compose

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.speechbuddy.compose.landing.LandingScreen
import com.example.speechbuddy.compose.login.LoginScreen
import com.example.speechbuddy.compose.resetpassword.ResetPassword
import com.example.speechbuddy.compose.resetpasswordcheck.ResetPasswordCheck
import com.example.speechbuddy.viewmodels.LoginViewModel

@Composable
fun SpeechBuddyApp() {
    val navController = rememberNavController()
    SpeechBuddyNavHost(
        navController = navController
    )
}

@Composable
fun SpeechBuddyNavHost(
    navController: NavHostController
) {
    // val activity = (LocalContext.current as Activity)
    NavHost(navController = navController, startDestination = "landing") {
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
        composable("resetpasswordcheck"){
            ResetPasswordCheck(
                onLoginClick = {
                    navController.navigate("login")
                },
                onNextClick = {
                    navController.navigate("resetpassword")
                }
            )
        }
        composable("resetpassword"){
            ResetPassword (
                onResetClick = {
                    navController.navigate("resetpasswordcheck")
                }
            )
        }
    }
}