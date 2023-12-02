package com.example.speechbuddy

import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.speechbuddy.compose.settings.DisplaySettings
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class DisplaySettingsScreenTest {

    private val androidTestUtil = AndroidTestUtil()

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = androidTestUtil.createAndroidIntentComposeRule<HomeActivity> {
        Intent(it, HomeActivity::class.java).apply {
            putExtra("isTest", true)
        }
    }

    @Before
    fun setUp() {
        hiltRule.inject()
        composeTestRule.activity.setContent {
            SpeechBuddyTheme(
                settingsRepository = composeTestRule.activity.settingsRepository,
                initialDarkMode = false
            ) {
                DisplaySettings(
                    paddingValues = PaddingValues()
                )
            }
        }
    }

    @Test
    fun should_display_all_elements_when_account_settings_screen_appears() {
        composeTestRule.onNodeWithText(DISPLAY).assertIsDisplayed()
        composeTestRule.onNodeWithText(DARK_MODE).assertIsDisplayed()
        composeTestRule.onNodeWithText(INITIAL_PAGE).assertIsDisplayed()
        composeTestRule.onNodeWithText(SYMBOL).assertIsDisplayed()
        composeTestRule.onNodeWithText(TTS).assertIsDisplayed()
    }

    @Test
    fun should_change_dark_mode_when_clicking_dark_mode() {
        composeTestRule.onNodeWithTag("SwitchTag").performClick()
    }

    companion object {
        const val DISPLAY = "디스플레이 설정"
        const val DARK_MODE = "다크 모드"
        const val INITIAL_PAGE = "시작 페이지"
        const val SYMBOL = "상징으로 말하기"
        const val TTS = "음성으로 말하기"
    }
}