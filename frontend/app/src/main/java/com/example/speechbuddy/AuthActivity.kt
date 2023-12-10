package com.example.speechbuddy

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.speechbuddy.compose.SpeechBuddyAuth
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.utils.Constants.Companion.GUEST_ID
import com.example.speechbuddy.utils.ResponseCode
import com.example.speechbuddy.worker.SeedDatabaseWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class AuthActivity : BaseActivity() {

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

    private fun subscribeObservers() {
        sessionManager.isAuthorized.observe(this) { isAuthorized ->
            if (isAuthorized) navHomeActivity()
        }
    }

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
    }

    private fun navHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getInitialDarkMode(): Boolean {
        var darkMode = false
        lifecycleScope.launch {
            settingsRepository.getDarkMode().collect {
                darkMode = it.data ?: false
            }
        }
        return darkMode
    }

    private suspend fun isBackupNecessary(): Boolean {
        val autoBackup = settingsRepository.getAutoBackup().first().data
        if (autoBackup == null || autoBackup == false) return false
        val lastBackupDate = settingsRepository.getLastBackupDate().first().data
        if (lastBackupDate == null || lastBackupDate == LocalDate.now().toString()) return false
        return true
    }

    private fun checkPreviousAuthUser() {
        lifecycleScope.launch {
            val resource = authRepository.checkPreviousUser().first()
            if (resource.data != null) {
                val userId = resource.data.first
                val authToken = resource.data.second
                val setAuthTokenJob = sessionManager.setAuthToken(authToken)
                setAuthTokenJob.join()

                if (userId != GUEST_ID && sessionManager.isLogin.value != true && isBackupNecessary())
                    autoBackup()
                sessionManager.setUserId(userId)
            } else {
                val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
                WorkManager.getInstance(applicationContext).enqueue(request)
            }
        }
    }

    // hides keyboard
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v != null) {
                v.clearFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun displayBackup() {
        CoroutineScope(Dispatchers.IO).launch {
            settingsRepository.displayBackup().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {
                        symbolListBackup()
                    }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        sessionManager.setIsLogin(false)
                        navHomeActivity()
                    }
                }
            }
        }
    }

    private fun symbolListBackup() {
        CoroutineScope(Dispatchers.IO).launch {
            settingsRepository.symbolListBackup().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {
                        favoriteSymbolBackup()
                    }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        sessionManager.setIsLogin(false)
                        navHomeActivity()
                    }
                }
            }
        }
    }

    private fun favoriteSymbolBackup() {
        CoroutineScope(Dispatchers.IO).launch {
            settingsRepository.favoriteSymbolBackup().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {
                        weightTableBackup()
                    }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        sessionManager.setIsLogin(false)
                        navHomeActivity()
                    }
                }
            }
        }
    }

    private fun weightTableBackup() {
        CoroutineScope(Dispatchers.IO).launch {
            settingsRepository.weightTableBackup().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {
                        sessionManager.setIsLogin(false)
                        settingsRepository.setLastBackupDate(LocalDate.now().toString())
                        navHomeActivity()
                    }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        sessionManager.setIsLogin(false)
                        navHomeActivity()
                    }
                }
            }
        }
    }

}