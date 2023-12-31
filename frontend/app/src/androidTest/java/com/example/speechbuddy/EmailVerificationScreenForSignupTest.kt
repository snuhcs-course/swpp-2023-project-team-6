package com.example.speechbuddy

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.speechbuddy.compose.emailverification.EmailVerificationScreen
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class EmailVerificationScreenForSignupTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<AuthActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeTestRule.activity.setContent {
            val fakeSource = "signup"
            SpeechBuddyTheme(
                settingsRepository = composeTestRule.activity.settingsRepository,
                initialDarkMode = false
            ) {
                EmailVerificationScreen(
                    source = fakeSource,
                    navigateCallback = {}
                )
            }
        }
    }

    @Test
    fun should_display_all_elements_when_emailverification_screen_appears() {
        composeTestRule.onNodeWithText(SIGNUP).assertIsDisplayed()
        composeTestRule.onNodeWithText(VERIFY_EMAIL_FOR_SIGNUP).assertIsDisplayed()
        composeTestRule.onNodeWithText(EMAIL).assertIsDisplayed()
        composeTestRule.onNodeWithText(SEND_CODE).assertIsDisplayed().assertHasClickAction().assertIsEnabled()
        composeTestRule.onNodeWithText(CODE).assertIsDisplayed()
        composeTestRule.onNodeWithText(NEXT).assertIsDisplayed().assertHasClickAction().assertIsNotEnabled()
    }

    // email related
    @Test
    fun should_display_no_email_error_message_after_clicking_send_code_with_no_input() {
        composeTestRule.onNodeWithText(SEND_CODE).performClick()
        composeTestRule.onNodeWithText(NO_EMAIL).assertIsDisplayed()
        composeTestRule.onNodeWithText(NEXT).assertIsDisplayed().assertHasClickAction().assertIsNotEnabled()
    }

    @Test
    fun should_display_wrong_email_error_message_after_clicking_send_code_with_invalid_email() {
        composeTestRule.onNodeWithText(EMAIL).performTextInput(INVALID_EMAIL)
        composeTestRule.onNodeWithText(SEND_CODE).performClick()
        composeTestRule.onNodeWithText(WRONG_EMAIL).assertIsDisplayed()
        composeTestRule.onNodeWithText(NEXT).assertIsDisplayed().assertHasClickAction().assertIsNotEnabled()
    }

    @Test
    fun should_display_email_already_taken_error_message_after_clicking_send_code_with_already_taken_email() {
        composeTestRule.onNodeWithText(EMAIL).performTextInput(ALREADY_TAKEN_EMAIL)
        composeTestRule.onNodeWithText(SEND_CODE).performClick()
        // wait for server's response
        Thread.sleep(10000)

        composeTestRule.onNodeWithText(EMAIL_ALREADY_TAKEN).assertIsDisplayed()
        composeTestRule.onNodeWithText(NEXT).assertIsDisplayed().assertHasClickAction().assertIsNotEnabled()
    }

    @Test
    fun should_enable_next_button_and_disable_email_field_after_clicking_send_code_with_valid_email() {
        composeTestRule.onNodeWithText(EMAIL).performTextInput(VALID_EMAIL)
        composeTestRule.onNodeWithText(SEND_CODE).performClick()

        // wait for email to be sent
        Thread.sleep(10000)

        composeTestRule.onNodeWithText(CODE_SUCCESSFULLY_SENT).assertIsDisplayed()
        composeTestRule.onNodeWithText(EMAIL).assertIsDisplayed().assertIsNotEnabled()
        composeTestRule.onNodeWithText(NEXT).assertIsDisplayed().assertHasClickAction().assertIsEnabled()
    }

    // code related
    @Test
    fun should_display_no_code_error_message_after_clicking_send_code_with_empty_code() {
        composeTestRule.onNodeWithText(EMAIL).performTextInput(VALID_EMAIL)
        composeTestRule.onNodeWithText(SEND_CODE).performClick()
        // wait for email to be sent
        Thread.sleep(10000)

        composeTestRule.onNodeWithText(NEXT).performClick()
        composeTestRule.onNodeWithText(NO_CODE).assertIsDisplayed()
    }

    @Test
    fun should_display_wrong_code_error_message_after_clicking_send_code_with_wrong_code() {
        composeTestRule.onNodeWithText(EMAIL).performTextInput(VALID_EMAIL)
        composeTestRule.onNodeWithText(SEND_CODE).performClick()
        // wait for email to be sent
        Thread.sleep(10000)

        composeTestRule.onNodeWithText(CODE).performTextInput(INVALID_CODE)
        composeTestRule.onNodeWithText(NEXT).performClick()
        composeTestRule.onNodeWithText(WRONG_CODE).assertIsDisplayed()
    }

    companion object {
        const val SIGNUP = "회원가입"
        const val VERIFY_EMAIL_FOR_SIGNUP = "회원가입을 위해 이메일 인증을 먼저 진행해주세요"
        const val EMAIL = "이메일"
        const val SEND_CODE = "인증번호 발송"
        const val CODE = "인증번호"
        const val NEXT = "다음"
        const val CODE_SUCCESSFULLY_SENT = "인증번호를 발송했습니다"

        // error or qualification messages
        const val NO_EMAIL = "이메일 주소를 입력해주세요"
        const val WRONG_EMAIL = "이메일 주소가 올바르지 않습니다"
        const val EMAIL_ALREADY_TAKEN = "다른 사용자가 이미 사용하고 있는 이메일입니다"
        const val NO_CODE = "인증번호를 입력해주세요"
        const val WRONG_CODE = "인증번호가 올바르지 않습니다"

        // inputs
        const val INVALID_EMAIL = "invalidemail"
        const val ALREADY_TAKEN_EMAIL = "chess109@snu.ac.kr"
        const val VALID_EMAIL = "valid@test.com"
        const val INVALID_CODE = "invalid"
    }

}