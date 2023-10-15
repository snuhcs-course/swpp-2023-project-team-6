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
import com.example.speechbuddy.compose.signup.SignupScreen

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
                onBackClick = {
                    navController.navigateUp()
                },
                onLoginClick = {},
                onResetPasswordClick = {
                    navController.navigate("resetpasswordcheck")
                },
                onSignupClick = {
                    navController.navigate("signup")
                }
            )
        }
        composable("signup") {
            SignupScreen(
                onSignupClick = { /*TODO*/ },
                onPreviousClick = { /*TODO*/ },
                email = ""
            )
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
            ResetPassword(
                onResetClick = {
                    navController.navigate("resetpasswordcheck")
                }
            )
        }
    }
}