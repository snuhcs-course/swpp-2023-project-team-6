package com.example.speechbuddy

import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.onNodeWithText
import com.example.speechbuddy.compose.settings.GuestSettings
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class GuestSettingsScreenTest {
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
        val guestId = -1
        val fakeEmail = "guest"
        val fakeNickname = "guest"
        composeTestRule.activity.sessionManager.setUserId(guestId)
        composeTestRule.activity.userRepository.setMyInfo(guestId, fakeEmail, fakeNickname)
        composeTestRule.activity.setContent {
            SpeechBuddyTheme(
                settingsRepository = composeTestRule.activity.settingsRepository,
                initialDarkMode = false
            ) {
                GuestSettings(
                    paddingValues = PaddingValues()
                )
            }
        }
    }

    @Test
    fun should_display_all_elements_when_account_settings_screen_appears() {
        composeTestRule.onNodeWithText(ACCOUNT).assertIsDisplayed()
        composeTestRule.onNodeWithText(ACCOUNT_GUEST_MODE).assertIsDisplayed()
        composeTestRule.onNodeWithText(EXIT).assertIsDisplayed().assertIsEnabled().assertHasClickAction()
    }

    @After
    fun tearDown() {
        composeTestRule.activityRule.scenario.close()
    }

    companion object {
        const val ACCOUNT = "계정"
        const val ACCOUNT_GUEST_MODE = "현재 게스트 모드를 사용 중입니다."
        const val EXIT = "게스트 모드에서 나가기"
    }
}