package com.example.speechbuddy

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.speechbuddy.LoginScreenTest.Companion.assertBackgroundColor
import com.example.speechbuddy.compose.settings.DisplaySettings
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class DisplaySettingsScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<AuthActivity>()

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
        composeTestRule.onNodeWithTag("dark_mode").assertIsDisplayed().assertIsEnabled()
        composeTestRule.onNodeWithTag("initial_page_symbol").assertIsDisplayed().assertIsEnabled()
        composeTestRule.onNodeWithTag("initial_page_tts").assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun should_change_dark_mode_when_clicking_dark_mode() {
        composeTestRule.onNodeWithTag("dark_mode").assertIsOff()
        composeTestRule.onNodeWithTag("dark_mode").performClick()
        composeTestRule.onNodeWithTag("dark_mode").assertIsOn()
        composeTestRule.onNodeWithText(DISPLAY).assertBackgroundColor(DARK_COLOR)
        composeTestRule.onNodeWithTag("dark_mode").performClick()
        composeTestRule.onNodeWithTag("dark_mode").assertIsOff()
        composeTestRule.onNodeWithText(DISPLAY).assertBackgroundColor(LIGHT_COLOR)
    }

    @Test
    fun should_change_initial_page_when_clicking_initial_page() {
        composeTestRule.onNodeWithTag("initial_page_symbol").assertIsSelected()
        composeTestRule.onNodeWithTag("initial_page_tts").assertIsNotSelected()
        composeTestRule.onNodeWithTag("initial_page_tts").performClick()
        composeTestRule.onNodeWithTag("initial_page_symbol").assertIsNotSelected()
        composeTestRule.onNodeWithTag("initial_page_tts").assertIsSelected()
        composeTestRule.onNodeWithTag("initial_page_symbol").performClick()
        composeTestRule.onNodeWithTag("initial_page_symbol").assertIsSelected()
        composeTestRule.onNodeWithTag("initial_page_tts").assertIsNotSelected()
    }

    @After
    fun tearDown() {
        composeTestRule.activityRule.scenario.close()
    }

    companion object {
        const val DISPLAY = "디스플레이 설정"
        const val DARK_MODE = "다크 모드"
        const val INITIAL_PAGE = "시작 페이지"
        const val SYMBOL = "상징으로 말하기"
        const val TTS = "음성으로 말하기"

        val LIGHT_COLOR = Color(0xFF000000)
        val DARK_COLOR = Color(0xFFFFFFFF)
    }
}