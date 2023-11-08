package com.example.speechbuddy

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.compose.SpeechBuddyHome
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Displaying edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {
            SpeechBuddyTheme {
                SpeechBuddyHome()
            }
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {
        sessionManager.cachedToken.observe(this) { authToken ->
            if (authToken == null) navAuthActivity()
        }
    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

}