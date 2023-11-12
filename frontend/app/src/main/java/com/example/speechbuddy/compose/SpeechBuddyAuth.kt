package com.example.speechbuddy.compose

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.speechbuddy.compose.emailverification.EmailVerificationScreen
import com.example.speechbuddy.compose.landing.LandingScreen
import com.example.speechbuddy.compose.login.LoginScreen
import com.example.speechbuddy.compose.resetpassword.ResetPasswordScreen
import com.example.speechbuddy.compose.signup.SignupScreen

@Composable
fun SpeechBuddyAuth() {
    val navController = rememberNavController()
    SpeechBuddyAuthNavHost(
        navController = navController
    )
}

@Composable
fun SpeechBuddyAuthNavHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = "landing") {
        composable("landing") {
            LandingScreen(
                onGuestClick = {
                    /* TODO: Implement guest mode */
                },
                onLoginClick = {
                    navController.navigate("login")
                }
            )
        }
        composable("login") {
            LoginScreen(
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
                navController = navController
            )
        }
        composable("signup/{emailInput}") { backStackEntry ->
            val emailInput = backStackEntry.arguments?.getString("emailInput")
            SignupScreen(
                email = emailInput ?: "",
                navController = navController
            )
        }
        composable("reset_password") {
            ResetPasswordScreen(
                navController = navController
            )
        }
    }
}