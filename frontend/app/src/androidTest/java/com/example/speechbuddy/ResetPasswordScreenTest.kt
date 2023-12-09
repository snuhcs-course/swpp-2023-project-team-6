package com.example.speechbuddy

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.speechbuddy.compose.resetpassword.ResetPasswordScreen
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ResetPasswordScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

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
                ResetPasswordScreen(
                    navigateToLogin = {}
                )
            }
        }
    }

    @Test
    fun should_display_all_elements_when_reset_password_screen_appears() {
        composeTestRule.onNodeWithText(RESET_PASSWORD).assertIsDisplayed()
        composeTestRule.onNodeWithText(RESET_PASSWORD_DESCRIPTION).assertIsDisplayed()
        composeTestRule.onNodeWithText(NEW_PASSWORD).assertIsDisplayed()
        composeTestRule.onNodeWithText(PASSWORD_TOO_SHORT).assertIsDisplayed()
        composeTestRule.onNodeWithText(NEW_PASSWORD_CHECK).assertIsDisplayed()
        composeTestRule.onNodeWithText(NEXT).assertIsDisplayed().assertHasClickAction().assertIsEnabled()
    }

    @Test
    fun should_display_no_password_error_message_after_clicking_next_with_no_password_input() {
        composeTestRule.onNodeWithText(NEXT).performClick()
        composeTestRule.onNodeWithText(NO_PASSWORD).assertIsDisplayed()
    }

    @Test
    fun should_display_no_password_error_message_after_clicking_next_with_only_check_password_input() {
        composeTestRule.onNodeWithText(NEW_PASSWORD_CHECK).performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithText(NEXT).performClick()
        composeTestRule.onNodeWithText(NO_PASSWORD).assertIsDisplayed()
    }

    @Test
    fun should_display_short_password_error_message_after_clicking_next_with_short_password() {
        composeTestRule.onNodeWithText(NEW_PASSWORD).performTextInput(SHORT_PASSWORD)
        composeTestRule.onNodeWithText(NEXT).performClick()
        composeTestRule.onNodeWithText(PASSWORD_TOO_SHORT).assertIsDisplayed()
    }

    @Test
    fun should_display_short_password_error_message_after_clicking_next_with_short_password_even_with_valid_check_password() {
        composeTestRule.onNodeWithText(NEW_PASSWORD).performTextInput(SHORT_PASSWORD)
        composeTestRule.onNodeWithText(NEW_PASSWORD_CHECK).performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithText(NEXT).performClick()
        composeTestRule.onNodeWithText(PASSWORD_TOO_SHORT).assertIsDisplayed()
    }

    @Test
    fun should_display_wrong_password_check_error_message_after_clicking_next_with_different_password_inputs() {
        composeTestRule.onNodeWithText(NEW_PASSWORD).performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithText(NEW_PASSWORD_CHECK).performTextInput(NO_MATCH_PASSWORD)
        composeTestRule.onNodeWithText(NEXT).performClick()
        composeTestRule.onNodeWithText(PASSWORD_TOO_SHORT).assertIsDisplayed()
    }

    companion object {
        const val RESET_PASSWORD = "비밀번호 변경"
        const val RESET_PASSWORD_DESCRIPTION = "앞으로 사용할 새 비밀번호를 입력해주세요"
        const val NEW_PASSWORD = "새 비밀번호"
        const val NEW_PASSWORD_CHECK = "새 비밀번호 확인"
        const val NEXT = "다음"

        // error or qualification messages
        const val NO_PASSWORD = "비밀번호를 입력해주세요"
        const val PASSWORD_TOO_SHORT = "비밀번호는 8자 이상이어야 합니다"
        const val WRONG_PASSWORD_CHECK = "비밀번호가 일치하지 않습니다"

        // inputs
        const val VALID_PASSWORD = "validPassword"
        const val SHORT_PASSWORD = "short"
        const val NO_MATCH_PASSWORD = "validPassword~"
    }
}
