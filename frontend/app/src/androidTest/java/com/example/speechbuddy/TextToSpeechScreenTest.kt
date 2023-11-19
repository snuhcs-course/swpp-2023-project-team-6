package com.example.speechbuddy

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.compose.texttospeech.TextToSpeechScreen
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class TextToSpeechScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<AuthActivity>()

    @Before
    fun setUp() {
        composeTestRule.activity.setContent {
            hiltRule.inject()
            SpeechBuddyTheme {
                TextToSpeechScreen(
                    bottomPaddingValues = PaddingValues(16.dp)
                )
            }
        }
    }

    @Test
    fun should_display_all_elements_when_text_to_speech_screen_appears() {
        composeTestRule.onNodeWithText(TALK_WITH_SPEECH).assertIsDisplayed()
        composeTestRule.onNodeWithText(TTS_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(TTS_DESCRIPTION).assertIsDisplayed()
        composeTestRule.onNode(hasSetTextAction()).assertIsDisplayed() //OutlinedTextField
        composeTestRule.onNodeWithText(PLAY_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(CLEAR_TEXT).assertIsDisplayed()
    }

    @Test
    fun should_disable_play_button_and_clear_button_when_text_is_empty() {
        composeTestRule.onNodeWithText(PLAY_TEXT).assertHasNoClickAction()
        composeTestRule.onNodeWithText(CLEAR_TEXT).assertHasNoClickAction()
    }

    @Test
    fun should_enable_play_button_and_clear_button_when_text_is_entered() {
        composeTestRule.onNode(hasSetTextAction()).performTextInput(SAMPLE_TEXT)
        composeTestRule.onNodeWithText(PLAY_TEXT).assertHasClickAction()
        composeTestRule.onNodeWithText(CLEAR_TEXT).assertHasClickAction()
    }

    @Test
    fun should_clear_text_when_clear_button_is_pressed() {
        composeTestRule.onNode(hasSetTextAction()).performTextInput(SAMPLE_TEXT)
        composeTestRule.onNodeWithText(CLEAR_TEXT).performClick()
        composeTestRule.onNode(hasSetTextAction()).assertTextEquals(EMPTY_TEXT)
    }

    @Test
    fun should_change_button_status_when_play_and_stop_are_clicked() {
        composeTestRule.onNode(hasSetTextAction()).performTextInput(SAMPLE_TEXT)
        composeTestRule.onNodeWithText(PLAY_TEXT).performClick()
        composeTestRule.onNodeWithText(STOP_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(STOP_TEXT).performClick()
        composeTestRule.onNodeWithText(PLAY_TEXT).assertIsDisplayed() //
    }

    @Test
    fun should_enable_stop_button_and_disable_clear_button_when_playing() {
        composeTestRule.onNode(hasSetTextAction()).performTextInput(SAMPLE_TEXT)
        composeTestRule.onNodeWithText(PLAY_TEXT).performClick()
        composeTestRule.onNodeWithText(STOP_TEXT).assertHasClickAction()
        composeTestRule.onNodeWithText(CLEAR_TEXT).assertHasNoClickAction()
    }

    companion object {
        const val TALK_WITH_SPEECH = "음성으로 말하기"
        const val TTS_TEXT = "소리로 말해요"
        const val TTS_DESCRIPTION = "텍스트를 입력하면 SpeechBuddy가 직접 읽어줘요"
        const val PLAY_TEXT = "재생"
        const val CLEAR_TEXT = "지우기"
        const val STOP_TEXT = "정지"

        const val EMPTY_TEXT = ""
        const val SAMPLE_TEXT = "소개원실 들어서 행복해요"
    }
}