package com.example.speechbuddy

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.speechbuddy.compose.settings.AccountSettings
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AccountSettingsScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidIntentComposeRule<HomeActivity> {
        Intent(it, HomeActivity::class.java).apply {
            putExtra("isTest", true)
        }
    }

    @Before
    fun setUp() {
        hiltRule.inject()
        val fakeId = 1
        val fakeEmail = "email@email.com"
        val fakeNickname = "nickname"
        composeTestRule.activity.sessionManager.setUserId(fakeId)
        composeTestRule.activity.userRepository.setMyInfo(fakeId, fakeEmail, fakeNickname)
        composeTestRule.activity.setContent {
            SpeechBuddyTheme(
                settingsRepository = composeTestRule.activity.settingsRepository,
                initialDarkMode = false
            ) {
                AccountSettings(
                    paddingValues = PaddingValues()
                )
            }
        }
    }

    @Test
    fun should_display_all_elements_when_account_settings_screen_appears() {

        composeTestRule.onNodeWithText(ACCOUNT).assertIsDisplayed()
        composeTestRule.onNodeWithText(EMAIL).assertIsDisplayed()
        composeTestRule.onNodeWithText(NICKNAME).assertIsDisplayed()
        composeTestRule.onNodeWithText(LOGOUT).assertIsDisplayed().assertHasClickAction().assertIsEnabled()
        composeTestRule.onNodeWithText(WITHDRAW).assertIsDisplayed().assertHasClickAction().assertIsEnabled()
    }

    companion object {
        const val ACCOUNT = "계정"
        const val EMAIL = "이메일"
        const val NICKNAME = "닉네임"
        const val LOGOUT = "로그아웃"
        const val WITHDRAW = "회원탈퇴"

        val PRIMARY_COLOR = Color(0xFF0D6D35)
        val SECONDARY_CONTAINER_COLOR = Color(0xFFD3E8D2)

        const val USER_EMAIL = "test@test.com"
        const val USER_NICKNAME = "test"

        fun SemanticsNodeInteraction.assertBackgroundColor(expectedBackground: Color) {
            val capturedName = captureToImage().colorSpace.name
            Assert.assertEquals(expectedBackground.colorSpace.name, capturedName)
        }
    }

    /**
     * Factory method to provide Android specific implementation of createComposeRule, for a given
     * activity class type A that needs to be launched via an intent.
     *
     * @param intentFactory A lambda that provides a Context that can used to create an intent. A intent needs to be returned.
     */
    inline fun <A: ComponentActivity> createAndroidIntentComposeRule(intentFactory: (context: Context) -> Intent) : AndroidComposeTestRule<ActivityScenarioRule<A>, A> {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = intentFactory(context)

        return AndroidComposeTestRule(
            activityRule = ActivityScenarioRule(intent),
            activityProvider = { scenarioRule -> scenarioRule.getActivity() }
        )
    }

    /**
     * Gets the activity from a scenarioRule.
     *
     * https://androidx.tech/artifacts/compose.ui/ui-test-junit4/1.0.0-alpha11-source/androidx/compose/ui/test/junit4/AndroidComposeTestRule.kt.html
     */
    fun <A : ComponentActivity> ActivityScenarioRule<A>.getActivity(): A {
        var activity: A? = null

        scenario.onActivity { activity = it }

        return activity ?: throw IllegalStateException("Activity was not set in the ActivityScenarioRule!")
    }
}