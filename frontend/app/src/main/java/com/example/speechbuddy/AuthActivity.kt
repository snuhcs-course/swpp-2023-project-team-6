package com.example.speechbuddy

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.speechbuddy.compose.SpeechBuddyAuth
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.viewmodel.DisplaySettingsViewModel
import com.example.speechbuddy.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val displaySettingsViewModel: DisplaySettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subscribeObservers()
        checkPreviousAuthUser()

        setContent {
            SpeechBuddyTheme(
                settingsRepository = settingsRepository,
                initialDarkMode = getInitialDarkMode()
            ) {
                SpeechBuddyAuth()
            }
        }
    }

    private fun subscribeObservers() {
        sessionManager.isAuthorized.observe(this) { isAuthorized ->
            if (isAuthorized) navHomeActivity()
        }
    }

    private fun navHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getInitialDarkMode(): Boolean {
        return displaySettingsViewModel.getDarkMode()
    }

    private fun checkPreviousAuthUser() {
        loginViewModel.checkPreviousUser()
    }

    // hides keyboard
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v != null) {
                v.clearFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
            }
        }
        return super.dispatchTouchEvent(event)
    }

}