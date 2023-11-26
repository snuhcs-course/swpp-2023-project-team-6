package com.example.speechbuddy

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import com.example.speechbuddy.compose.SpeechBuddyAuth
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.utils.ResponseCode
import com.example.speechbuddy.viewmodel.DisplaySettingsViewModel
import com.example.speechbuddy.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : BaseActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val displaySettingsViewModel: DisplaySettingsViewModel by viewModels()
    private val isBackupCompleted = mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subscribeObservers()
        checkPreviousAuthUser()

        setContent {
            SpeechBuddyTheme(
                settingsRepository = settingsRepository,
                initialDarkMode = getInitialDarkMode()
            ) {
                SpeechBuddyAuth(isBackup = false)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun subscribeObservers() {
        sessionManager.isAuthorized.observe(this) { isAuthorized ->
            if (isAuthorized) autoBackup()
            // 로그인 아님, 게스트 아님, 날짜 지남
            else navHomeActivity()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun autoBackup() {
        setContent {
            SpeechBuddyTheme(
                settingsRepository = settingsRepository,
                initialDarkMode = getInitialDarkMode()
            ) {
                SpeechBuddyAuth(isBackup = true)
            }
        }
        displayBackup()
        if (isBackupCompleted.value) navHomeActivity()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayBackup() {
        lifecycleScope.launch {
            settingsRepository.displayBackup().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> { symbolListBackup() }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> { isBackupCompleted.value = true }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun symbolListBackup() {
        lifecycleScope.launch {
            settingsRepository.symbolListBackup().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> { favoriteSymbolBackup() }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> { isBackupCompleted.value = true }
                }

            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun favoriteSymbolBackup() {
        lifecycleScope.launch {
            settingsRepository.favoriteSymbolBackup().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> { weightTableBackup() }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> { isBackupCompleted.value = true }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun weightTableBackup() {
        lifecycleScope.launch {
            settingsRepository.weightTableBackup().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> { isBackupCompleted.value = true }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> { isBackupCompleted.value = true }
                }
            }
        }
    }

}