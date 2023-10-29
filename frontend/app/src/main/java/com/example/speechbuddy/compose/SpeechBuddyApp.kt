package com.example.speechbuddy.compose

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.speechbuddy.compose.emailverification.EmailVerificationScreen
import com.example.speechbuddy.compose.home.HomeScreen
import com.example.speechbuddy.compose.landing.LandingScreen
import com.example.speechbuddy.compose.login.LoginScreen
import com.example.speechbuddy.compose.resetpassword.ResetPasswordScreen
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
    NavHost(navController = navController, startDestination = "home") {
        composable("landing") {
            LandingScreen(
                onLoginClick = {
                    navController.navigate("login")
                },
                onGuestClick = {
                    navController.navigate("text_to_speech")
                }
            )
        }
        composable("login") {
            LoginScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onResetPasswordClick = {
                    navController.navigate("email_verification/reset_password")
                },
                onSignupClick = {
                    navController.navigate("email_verification/signup")
                }
            )
        }
        composable("email_verification/{source}") { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source")
            EmailVerificationScreen(
                source = source,
                onBackClick = {
                    navController.navigateUp()
                },
                navController = navController,
            )
        }
        composable("signup/{emailInput}") { backStackEntry ->
            val emailInput = backStackEntry.arguments?.getString("emailInput")
            SignupScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                email = emailInput ?: ""
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