package com.example.speechbuddy

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.speechbuddy.compose.SpeechBuddyHome
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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

        // Call method to copy the file
        copyFileFromAssetsToInternalStorage("weight_table.txt")

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