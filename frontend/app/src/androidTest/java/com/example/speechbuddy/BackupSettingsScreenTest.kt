package com.example.speechbuddy

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.speechbuddy.compose.settings.BackupSettings
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class BackupSettingsScreenTest {

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
                BackupSettings(
                    paddingValues = PaddingValues()
                )
            }
        }
    }

    @Test
    fun should_display_all_elements_when_account_settings_screen_appears() {
        composeTestRule.onNodeWithText(BACKUP_TO_SERVER).assertIsDisplayed()
        composeTestRule.onNodeWithText(LAST_BACKUP_DATE).assertIsDisplayed()
        composeTestRule.onNodeWithText(ENABLE_AUTO_BACKUP).assertIsDisplayed()
        composeTestRule.onNodeWithText(BACKUP_NOW).assertIsDisplayed().assertIsEnabled().assertHasClickAction()
        composeTestRule.onNodeWithTag("auto_backup").assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun should_change_auto_backup_when_switch_is_clicked() {
        composeTestRule.onNodeWithTag("auto_backup").assertIsOn()
        composeTestRule.onNodeWithTag("auto_backup").performClick()
        composeTestRule.onNodeWithTag("auto_backup").assertIsOff()
        composeTestRule.onNodeWithTag("auto_backup").performClick()
        composeTestRule.onNodeWithTag("auto_backup").assertIsOn()
    }

    @Test
    fun should_show_loading_indicator_when_backup_is_clicked() {
        composeTestRule.onNodeWithText(BACKUP_NOW).performClick()
        composeTestRule.onNodeWithTag("backup_loading").assertIsDisplayed()
    }

    companion object {
        const val BACKUP_TO_SERVER = "서버에 백업하기"
        const val LAST_BACKUP_DATE = "마지막 백업 날짜"
        const val ENABLE_AUTO_BACKUP = "자동 백업 활성화"
        const val BACKUP_NOW = "지금 백업하기"
    }

}