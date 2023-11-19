package com.example.speechbuddy

import android.app.Activity
import android.app.Instrumentation
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.example.speechbuddy.compose.symbolcreation.SymbolCreationScreen
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@HiltAndroidTest
class SymbolCreationScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<AuthActivity>()

    @Before
    fun setup() {
        Intents.init()
        composeTestRule.activity.setContent {
            hiltRule.inject()
            SpeechBuddyTheme {
                SymbolCreationScreen(
                    bottomPaddingValues = PaddingValues(16.dp)
                )
            }
        }
    }

    @After
    fun cleanup() {
        Intents.release()
    }
    
    @Test
    fun should_select_photo_when_add_photo_button_is_clicked() {
        //Arrange
        val resultData = Intent().apply {
            data = Uri.parse("content://path/to/photo")
        }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)

        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream)
        val byteArrayInputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())

        val contentResolver = mockk<ContentResolver>()
        every { contentResolver.openInputStream(any()) } returns byteArrayInputStream

        // Act
        composeTestRule.onNodeWithContentDescription(PHOTO_ICON_DESCRIPTION).performClick()
        // Assert
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT))
        // Add more assertions to check that the photo was correctly loaded
    }

    companion object {
        const val PHOTO_ICON_DESCRIPTION = "새 상징 만들기"
    }
}