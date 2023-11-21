package com.example.speechbuddy

import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.speechbuddy.compose.login.LoginScreen
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
class LoginScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<AuthActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeTestRule.activity.setContent {
            SpeechBuddyTheme {
                LoginScreen(
                    onResetPasswordClick = {},
                    onSignupClick = {}
                )
            }
        }
    }

    @Test
    fun should_display_all_elements_when_login_screen_appears() {
        composeTestRule.onAllNodesWithText(LOGIN)[0].assertIsDisplayed()
        composeTestRule.onNodeWithText(LOGIN_DESCRIPTION).assertIsDisplayed()
        composeTestRule.onNodeWithText(EMAIL).assertIsDisplayed()
        composeTestRule.onNodeWithText(PASSWORD).assertIsDisplayed()
        composeTestRule.onAllNodesWithText(LOGIN)[1].assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText(FORGOT_PASSWORD).assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText(SIGNUP_ACTION).assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun should_display_no_email_error_message_with_no_input_for_email() {
        composeTestRule.onAllNodesWithText(LOGIN)[1].performClick()
        composeTestRule.onNodeWithText(NO_EMAIL).assertIsDisplayed()
    }

    @Test
    fun should_display_no_password_error_message_with_no_input_for_password() {
        composeTestRule.onNodeWithText(EMAIL).performTextInput(VALID_EMAIL)
        composeTestRule.onAllNodesWithText(LOGIN)[1].performClick()
        composeTestRule.onNodeWithText(NO_PASSWORD).assertIsDisplayed()
    }

    @Test
    fun should_display_wrong_email_error_message_with_invalid_email_input() {
        composeTestRule.onNodeWithText(EMAIL).performTextInput(INVALID_EMAIL)
        composeTestRule.onNodeWithText(PASSWORD).performTextInput(VALID_PASSWORD)
        composeTestRule.onAllNodesWithText(LOGIN)[1].performClick()
        composeTestRule.onNodeWithText(WRONG_EMAIL).assertIsDisplayed()
    }

    @Test
    fun should_display_wrong_email_error_message_with_invalid_email_input_even_with_no_password_input() {
        composeTestRule.onNodeWithText(EMAIL).performTextInput(INVALID_EMAIL)
        composeTestRule.onAllNodesWithText(LOGIN)[1].performClick()
        composeTestRule.onNodeWithText(WRONG_EMAIL).assertIsDisplayed()
    }

    @Test
    fun should_display_wrong_password_error_message_with_invalid_password_input() {
        composeTestRule.onNodeWithText(EMAIL).performTextInput(VALID_EMAIL)
        composeTestRule.onNodeWithText(PASSWORD).performTextInput(INVALID_PASSWORD)
        composeTestRule.onAllNodesWithText(LOGIN)[1].performClick()
        composeTestRule.onNodeWithText(WRONG_PASSWORD).assertIsDisplayed()
    }

    @Test
    fun should_display_wrong_password_error_message_with_wrong_password_input() {
        composeTestRule.onNodeWithText(EMAIL).performTextInput(REGISTERED_EMAIL)
        composeTestRule.onNodeWithText(PASSWORD).performTextInput(UNREGISTERED_PASSWORD)
        composeTestRule.onAllNodesWithText(LOGIN)[1].performClick()
        composeTestRule.onNodeWithText(WRONG_PASSWORD).assertIsDisplayed()
    }

    @Test
    fun should_change_button_color_when_invalid_input_and_valid_input() {
        composeTestRule.onNodeWithText(EMAIL).performTextInput(INVALID_EMAIL)
        composeTestRule.onNodeWithText(PASSWORD).performTextInput(INVALID_PASSWORD)
        composeTestRule.onAllNodesWithText(LOGIN)[1].performClick()
        composeTestRule.onAllNodesWithText(LOGIN)[1].assertBackgroundColor(ERROR_COLOR)
        composeTestRule.onNodeWithText(FORGOT_PASSWORD).assertBackgroundColor(ERROR_CONTAINER_COLOR)
        composeTestRule.onNodeWithText(EMAIL).performTextInput(VALID_EMAIL)
        composeTestRule.onNodeWithText(PASSWORD).performTextInput(VALID_PASSWORD)
        composeTestRule.onAllNodesWithText(LOGIN)[1].assertBackgroundColor(PRIMARY_COLOR)
        composeTestRule.onNodeWithText(FORGOT_PASSWORD)
            .assertBackgroundColor(SECONDARY_CONTAINER_COLOR)
    }

    @Test
    fun should_change_textfield_color_when_invalid_input_and_valid_input() {
        composeTestRule.onNodeWithText(EMAIL).performTextInput(INVALID_EMAIL)
        composeTestRule.onNodeWithText(PASSWORD).performTextInput(INVALID_PASSWORD)
        composeTestRule.onAllNodesWithText(LOGIN)[1].performClick()
        composeTestRule.onAllNodesWithText(LOGIN)[1].assertBackgroundColor(ERROR_COLOR)
        composeTestRule.onNodeWithText(PASSWORD).assertBackgroundColor(ERROR_COLOR)
        composeTestRule.onNodeWithText(EMAIL).performTextInput(VALID_EMAIL)
        composeTestRule.onNodeWithText(PASSWORD).performTextInput(VALID_PASSWORD)
        composeTestRule.onAllNodesWithText(LOGIN)[1].assertBackgroundColor(PRIMARY_COLOR)
        composeTestRule.onNodeWithText(PASSWORD).assertBackgroundColor(PRIMARY_COLOR)
    }


    companion object {
        const val LOGIN = "로그인"
        const val LOGIN_DESCRIPTION = "로그인해 SpeechBuddy의 다양한 기능을 사용해보세요"
        const val EMAIL = "이메일"
        const val PASSWORD = "비밀번호"
        const val FORGOT_PASSWORD = "비밀번호를 잊으셨나요?"
        const val SIGNUP_ACTION = "회원가입"

        const val WRONG_EMAIL = "이메일 주소가 올바르지 않습니다"
        const val WRONG_PASSWORD = "비밀번호가 올바르지 않습니다"
        const val NO_EMAIL = "이메일 주소를 입력해주세요"
        const val NO_PASSWORD = "비밀번호를 입력해주세요"

        val ERROR_COLOR = Color(0xFFBA1A1A)
        val ERROR_CONTAINER_COLOR = Color(0xFFFFDAD6)
        val PRIMARY_COLOR = Color(0xFF0D6D35)
        val SECONDARY_CONTAINER_COLOR = Color(0xFFD3E8D2)

        const val VALID_EMAIL = "validemail@test.com"
        const val REGISTERED_EMAIL = "hahaa@gmail.com"
        const val INVALID_EMAIL = "invalid"
        const val VALID_PASSWORD = "asdfqwer1234"
        const val UNREGISTERED_PASSWORD = "merong"
        const val INVALID_PASSWORD = "invalid"

        fun SemanticsNodeInteraction.assertBackgroundColor(expectedBackground: Color) {
            val capturedName = captureToImage().colorSpace.name
            assertEquals(expectedBackground.colorSpace.name, capturedName)
        }
    }

}