package com.example.speechbuddy

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.speechbuddy.compose.landing.LandingScreen
import com.example.speechbuddy.repository.SettingsRepository
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.components.SingletonComponent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Singleton

@HiltAndroidTest
class LandingScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<AuthActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeTestRule.activity.setContent {
            SpeechBuddyTheme {
                LandingScreen( onLoginClick = {}, isBackup = false)
            }
        }
    }

    @Test
    fun should_display_guest_mode_and_login_buttons_when_landing_screen_appears() {
        composeTestRule.onNodeWithText(GUEST_MODE_ACTION).assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText(LOGIN_ACTION).assertIsDisplayed().assertHasClickAction()
    }

    companion object {
        const val GUEST_MODE_ACTION = "게스트 모드로 시작하기"
        const val LOGIN_ACTION = "로그인하기"
    }

}