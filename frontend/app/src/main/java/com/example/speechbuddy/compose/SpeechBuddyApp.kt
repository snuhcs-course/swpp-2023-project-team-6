package com.example.speechbuddy.compose

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.speechbuddy.compose.home.HomeScreen
import com.example.speechbuddy.compose.landing.LandingScreen
import com.example.speechbuddy.compose.login.LoginScreen
import com.example.speechbuddy.compose.resetpassword.EmailVerificationScreen
import com.example.speechbuddy.compose.resetpassword.ResetPasswordScreen
import com.example.speechbuddy.compose.signup.SignupScreen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
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
    NavHost(navController = navController, startDestination = "home") {
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
        composable("home") {
            HomeScreen()
        }
    }
}