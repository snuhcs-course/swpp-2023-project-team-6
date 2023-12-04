package com.example.speechbuddy

import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import com.example.speechbuddy.compose.settings.MainSettings
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainSettingsScreenTest {
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
        val fakeId = 0
        val fakeEmail = "email"
        val fakeNickname = "nickname"
        composeTestRule.activity.sessionManager.setUserId(fakeId)
        composeTestRule.activity.userRepository.setMyInfo(fakeId, fakeEmail, fakeNickname)
        composeTestRule.activity.setContent {
            SpeechBuddyTheme(
                settingsRepository = composeTestRule.activity.settingsRepository,
                initialDarkMode = false
            ) {
                MainSettings(
                    paddingValues = PaddingValues(),
                    navController = NavHostController(composeTestRule.activity.applicationContext)
                )
            }
        }
    }

    @Test
    fun should_display_all_elements_when_account_settings_screen_appears_in_guest_mode() {
        composeTestRule.onNodeWithText(ACCOUNT).assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText(DISPLAY).assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText(MY_SETTINGS).assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText(BACKUP).assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText(VERSION).assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText(DEVELOPER).assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText(COPYRIGHT).assertIsDisplayed().assertHasClickAction()
    }

    @After
    fun tearDown() {
        composeTestRule.activityRule.scenario.close()
    }

    companion object {
        const val ACCOUNT = "계정"
        const val DISPLAY = "디스플레이"
        const val MY_SETTINGS = "상징 목록 관리"
        const val BACKUP = "서버에 백업하기"
        const val VERSION = "버전 정보"
        const val DEVELOPER = "개발자 정보"
        const val COPYRIGHT = "저작권 정보"
    }

}