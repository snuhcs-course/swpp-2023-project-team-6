package com.example.speechbuddy

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import com.example.speechbuddy.compose.SpeechBuddyHome
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.viewmodel.DisplaySettingsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private val displaySettingsViewModel: DisplaySettingsViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Displaying edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val isBeingReloadedForDarkModeChange = intent.getBooleanExtra("isBeingReloadedForDarkModeChange", false)

        setContent {
            SpeechBuddyTheme(
                darkTheme = getDarkMode()
            ) {
                SpeechBuddyHome(getInitialPage(), isBeingReloadedForDarkModeChange)
            }
        }

        subscribeObservers()

        val previousDarkMode = getDarkMode()

        val darkModeObserver = Observer<Boolean?> { darkMode ->
            if (darkMode != previousDarkMode) {
                recreateHomeActivity()
            }
        }

        settingsRepository.darkModeLiveData.observeForever(darkModeObserver)

    }

    private fun subscribeObservers() {
        sessionManager.isAuthorized.observe(this) { isAuthorized ->
            if (!isAuthorized) navAuthActivity()
        }
    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun recreateHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("isBeingReloadedForDarkModeChange", true)
        startActivity(intent)
        finish()
    }

    private fun getDarkMode(): Boolean {
        return displaySettingsViewModel.getDarkMode()
    }

    private fun getInitialPage(): Boolean {
        return displaySettingsViewModel.getInitialPage()
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