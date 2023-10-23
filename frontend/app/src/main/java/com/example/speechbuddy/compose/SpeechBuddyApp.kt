package com.example.speechbuddy.compose

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.speechbuddy.compose.landing.LandingScreen
import com.example.speechbuddy.compose.login.LoginScreen
import com.example.speechbuddy.compose.emailverification.EmailVerificationScreen
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
                onSubmitClick = {
                },
                onNextClick = {
                    when (source) {
                        "reset_password" -> {
                            navController.navigate("reset_password")
                        }
                        "signup" -> {
                            navController.navigate("signup")
                        }
                    }
                }
            )
        }
        composable("signup") {
            SignupScreen(
                onBackClick = {
                    // has to re-verify one's email!
                    navController.navigate("login")
                },
                onSignupClick = {
                },
                email = ""
            )
        }
        composable("reset_password") {
            ResetPasswordScreen(
                onBackClick = {
                    // has to re-verify one's email!
                    navController.navigate("login")
                },
                onNextClick = {}
            )
        }
    }
}