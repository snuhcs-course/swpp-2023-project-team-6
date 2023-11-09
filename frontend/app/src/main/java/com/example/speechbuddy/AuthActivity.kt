package com.example.speechbuddy

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import com.example.speechbuddy.compose.SpeechBuddyAuth
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class AuthActivity : BaseActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Displaying edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        subscribeObservers()
        checkPreviousAuthUser()

        setContent {
            SpeechBuddyTheme {
                SpeechBuddyAuth()
            }
        }

        // Call method to copy the file
        copyFileFromAssetsToInternalStorage("weight_table.txt")

    }

    private fun subscribeObservers() {
        sessionManager.cachedToken.observe(this) { authToken ->
            if (authToken != null) navHomeActivity()
        }
    }

    private fun navHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkPreviousAuthUser() {
        loginViewModel.checkPreviousUser()
    }

    // need to move to sub thread later.
    // has probability to halt the process if file too large
    private fun copyFileFromAssetsToInternalStorage(filename: String) {
        val file = File(filesDir, filename)
        if (!file.exists()) {
            try {
                assets.open(filename).use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}