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
fun SpeechBuddyAuth(
    isBackup: Boolean
) {
    val navController = rememberNavController()
    SpeechBuddyAuthNavHost(
        navController = navController,
        isBackup = isBackup
    )
}

@Composable
fun SpeechBuddyAuthNavHost(
    navController: NavHostController,
    isBackup: Boolean
) {
    NavHost(navController = navController, startDestination = "landing") {
        composable("landing") {
            LandingScreen(
                onLoginClick = {
                    navController.navigate("login")
                },
                isBackup = isBackup
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
                navigateCallback = { navController.navigate(it) }
            )
        }
        composable("signup/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            SignupScreen(
                email = email ?: "",
                navigateToLogin = {
                    navController.navigate("login")
                }
            )
        }
        composable("reset_password") {
            ResetPasswordScreen(
                navigateToLogin = {
                    navController.navigate("login")
                }
            )
        }
    }
}