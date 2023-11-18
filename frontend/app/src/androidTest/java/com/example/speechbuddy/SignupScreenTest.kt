package com.example.speechbuddy

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.example.speechbuddy.compose.signup.SignupScreen
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SignupScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<AuthActivity>()

    @ExperimentalAnimationApi
    @Before
    fun setUp() {
        hiltRule.inject()
        composeTestRule.activity.setContent {
            val fakeEmail = "test@example.com"
            SpeechBuddyTheme {
                SignupScreen(
                    email = fakeEmail,
                    navigateToLogin = {}
                )
            }
        }
    }

    @Test
    fun should_display_all_elements_when_signup_screen_appears() {
        composeTestRule.onAllNodesWithText(SIGNUP)[0].assertIsDisplayed()
        composeTestRule.onNodeWithText(SIGNUP_DESCRIPTION).assertIsDisplayed()
        composeTestRule.onNodeWithText(NICKNAME).assertIsDisplayed()
        composeTestRule.onNodeWithText(NICKNAME_TOO_LONG).assertIsDisplayed()
        composeTestRule.onNodeWithText(PASSWORD).assertIsDisplayed()
        composeTestRule.onNodeWithText(PASSWORD_TOO_SHORT).assertIsDisplayed()
        composeTestRule.onNodeWithText(PASSWORD_CHECK).assertIsDisplayed()
        composeTestRule.onAllNodesWithText(SIGNUP)[1].assertIsDisplayed().assertHasClickAction()
    }

    // Before Click
//    @Test
    fun should_display_no_nickname_error_message_when_nickname_is_changed_to_empty_nickname() {
        composeTestRule.onNodeWithText(NICKNAME).performTextInput(VALID_NICKNAME)
        composeTestRule.onNodeWithText(NICKNAME).performTextClearance()
        composeTestRule.onNodeWithText(NO_NICKNAME).assertIsDisplayed()
    }

    @Test
    fun should_display_long_nickname_error_message_with_long_nickname() {
        composeTestRule.onNodeWithText(NICKNAME).performTextInput(LONG_NICKNAME)
        composeTestRule.onNodeWithText(NICKNAME_TOO_LONG).assertIsDisplayed()
    }

    @Test
    fun should_display_short_password_error_message_when_password_is_changed_to_empty_password() {
        composeTestRule.onNodeWithText(PASSWORD).performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithText(PASSWORD).performTextClearance()
        composeTestRule.onNodeWithText(PASSWORD_TOO_SHORT).assertIsDisplayed()
    }

    @Test
    fun should_display_short_password_error_message_with_short_password() {
        composeTestRule.onNodeWithText(PASSWORD).performTextInput(SHORT_PASSWORD)
        composeTestRule.onNodeWithText(PASSWORD_TOO_SHORT).assertIsDisplayed()
    }

    // After click
    @Test
    fun should_display_no_nickname_error_message_after_signup_click_with_no_input() {
        composeTestRule.onAllNodesWithText(SIGNUP)[1].performClick()
        composeTestRule.onNodeWithText(NO_NICKNAME).assertIsDisplayed()
    }

    @Test
    fun should_display_long_nickname_error_message_after_signup_click_with_long_nickname() {
        composeTestRule.onNodeWithText(NICKNAME).performTextInput(LONG_NICKNAME)
        composeTestRule.onAllNodesWithText(SIGNUP)[1].performClick()
        composeTestRule.onNodeWithText(NICKNAME_TOO_LONG).assertIsDisplayed()
    }

    @Test
    fun should_display_no_password_error_message_after_signup_click_with_no_password() {
        composeTestRule.onNodeWithText(NICKNAME).performTextInput(VALID_NICKNAME)
        composeTestRule.onAllNodesWithText(SIGNUP)[1].performClick()
        composeTestRule.onNodeWithText(NO_PASSWORD).assertIsDisplayed()
    }

    @Test
    fun should_display_short_password_error_message_after_signup_click_with_short_password() {
        composeTestRule.onNodeWithText(NICKNAME).performTextInput(VALID_NICKNAME)
        composeTestRule.onNodeWithText(PASSWORD).performTextInput(SHORT_PASSWORD)
        composeTestRule.onAllNodesWithText(SIGNUP)[1].performClick()
        composeTestRule.onNodeWithText(PASSWORD_TOO_SHORT).assertIsDisplayed()
    }

    @Test
    fun should_display_wrong_password_check_error_message_after_signup_click_with_different_password_inputs() {
        composeTestRule.onNodeWithText(NICKNAME).performTextInput(VALID_NICKNAME)
        composeTestRule.onNodeWithText(PASSWORD).performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithText(PASSWORD_CHECK).performTextInput(NO_MATCH_PASSWORD)
        composeTestRule.onAllNodesWithText(SIGNUP)[1].performClick()
        composeTestRule.onNodeWithText(WRONG_PASSWORD_CHECK).assertIsDisplayed()
    }

    companion object {
        const val SIGNUP = "회원가입"
        const val SIGNUP_DESCRIPTION = "SpeechBuddy에 오신 것을 환영합니다!"
        const val NICKNAME = "닉네임"
        const val PASSWORD = "비밀번호"
        const val PASSWORD_CHECK = "비밀번호 확인"

        // error or qualification messages
        const val NICKNAME_TOO_LONG = "닉네임은 15자 이하여야 합니다"
        const val NO_NICKNAME = "닉네임을 입력해주세요"
        const val NO_PASSWORD = "비밀번호를 입력해주세요"
        const val PASSWORD_TOO_SHORT = "비밀번호는 8자 이상이어야 합니다"
        const val WRONG_PASSWORD_CHECK = "비밀번호가 일치하지 않습니다"

        // inputs
        const val VALID_NICKNAME = "nickname"
        const val LONG_NICKNAME = "tooooolonggggnicknamehahaaaaaa"
        const val VALID_PASSWORD = "validPassword"
        const val SHORT_PASSWORD = "short"
        const val NO_MATCH_PASSWORD = "validPassword~"
    }
}
