package com.example.speechbuddy

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.speechbuddy.MainApplication.Companion.token_prefs
import com.example.speechbuddy.compose.SpeechBuddyApp
import com.example.speechbuddy.data.domain.TokenSharedPreferences
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        token_prefs = TokenSharedPreferences(applicationContext)
        super.onCreate(savedInstanceState)

        // Displaying edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SpeechBuddyTheme {
                SpeechBuddyApp()
            }
        }
    }
}