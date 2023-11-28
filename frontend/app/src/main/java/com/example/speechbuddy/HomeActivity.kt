package com.example.speechbuddy

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.speechbuddy.compose.SpeechBuddyHome
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.viewmodel.DisplaySettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {
            SpeechBuddyTheme(
                settingsRepository = settingsRepository,
                initialDarkMode = getInitialDarkMode()
            ) {
                SpeechBuddyHome(getInitialPage())
            }
        }

        subscribeObservers()
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

    private fun getInitialPage(): Boolean {
        var initialPage = false
        lifecycleScope.launch {
            settingsRepository.getInitialPage().collect {
                initialPage = it.data?: false
            }
        }
        return initialPage
    }

    private fun getInitialDarkMode(): Boolean {
        var darkMode = false
        lifecycleScope.launch {
            settingsRepository.getDarkMode().collect {
                darkMode = it.data?: false
            }
        }
        return darkMode
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