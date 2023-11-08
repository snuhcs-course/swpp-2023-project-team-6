package com.example.speechbuddy

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.speechbuddy.MainApplication.Companion.token_prefs
import com.example.speechbuddy.compose.SpeechBuddyApp
import com.example.speechbuddy.domain.utils.TokenSharedPreferences
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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
        // Call method to copy the file
        copyFileFromAssetsToInternalStorage("weight_table.txt");

    }

    // need to move to sub thread later.
    // has probability to halt the process if file too large
    private fun copyFileFromAssetsToInternalStorage(filename: String) {
        val file = File(filesDir, filename)
//        Log.d("test", "installed weight_table")
        if (!file.exists()) {
            try {
                assets.open(filename).use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace() // Handle the exception
            }
        }
    }
}