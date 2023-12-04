package com.example.speechbuddy

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.speechbuddy.compose.SpeechBuddyHome
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.worker.SeedDatabaseWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.speechbuddy.utils.Constants.Companion.GUEST_ID

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(sessionManager.userId.value==GUEST_ID) {
            // only activates if it is in guest mode.
            // does not activate when logged in since db is overwritten on login
            // force the database worker to build a new db
            // in order to check if the weight-db is empty or not and fill it
            Log.d("test", "home acrivity starting initialization of db")
            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
            WorkManager.getInstance(this).enqueue(request)
        }
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
            if (!isAuthorized && !intent.getBooleanExtra("isTest", false)) navAuthActivity()
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

}