package com.example.speechbuddy

import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.speechbuddy.compose.settings.AccountSettings
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.DelicateCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AccountSettingsScreenTest {

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
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(LOGOUT).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(ACCOUNT).assertIsDisplayed()
        composeTestRule.onNodeWithText(EMAIL).assertIsDisplayed()
        composeTestRule.onNodeWithText(NICKNAME).assertIsDisplayed()
        composeTestRule.onNodeWithText(LOGOUT).assertIsDisplayed().assertHasClickAction().assertIsEnabled()
        composeTestRule.onNodeWithText(WITHDRAW).assertIsDisplayed().assertHasClickAction().assertIsEnabled()
        composeTestRule.onNodeWithText(FAKE_EMAIL).assertIsDisplayed()
        composeTestRule.onNodeWithText(FAKE_NICKNAME).assertIsDisplayed()
    }

    @Test
    fun should_display_backup_dialog_when_clicking_backup_button() {
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(LOGOUT).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onAllNodesWithText(LOGOUT)[0].performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(BACKUP_DIALOG).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onAllNodesWithText(LOGOUT)[0].assertIsNotEnabled()
        composeTestRule.onNodeWithText(WITHDRAW).assertIsNotEnabled()
        composeTestRule.onNodeWithText(BACKUP).assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText(BACKUP_DIALOG).assertIsDisplayed()
        composeTestRule.onAllNodesWithText(LOGOUT)[1].assertIsDisplayed()
        composeTestRule.onAllNodesWithText(LOGOUT)[2].assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun should_remove_dialog_when_clicking_backup_button_in_backup_dialog() {
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(LOGOUT).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(LOGOUT).performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(BACKUP_DIALOG).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(BACKUP).performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(BACKUP_DIALOG).fetchSemanticsNodes().isEmpty()
        }
        composeTestRule.onNodeWithText(BACKUP_DIALOG).assertDoesNotExist()
        composeTestRule.onNodeWithText(BACKUP).assertDoesNotExist()
        composeTestRule.onNodeWithText(LOGOUT).assertIsNotEnabled()
        composeTestRule.onNodeWithText(WITHDRAW).assertIsNotEnabled()
    }

    @Test
    fun should_display_logout_dialog_when_clicking_logout_button_in_backup_dialog() {
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(LOGOUT).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(LOGOUT).performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(BACKUP_DIALOG).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onAllNodesWithText(LOGOUT)[0].assertIsNotEnabled()
        composeTestRule.onAllNodesWithText(LOGOUT)[1].assertIsDisplayed()
        composeTestRule.onAllNodesWithText(LOGOUT)[2].assertHasClickAction().assertIsEnabled()
        composeTestRule.onAllNodesWithText(LOGOUT)[2].performClick()
        composeTestRule.waitUntil {
            composeTestRule.onAllNodesWithText(LOGOUT_DIALOG).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(CANCEL).assertIsDisplayed()
        composeTestRule.onNodeWithText(LOGOUT_DIALOG).assertIsDisplayed()
        composeTestRule.onAllNodesWithText(LOGOUT)[0].assertIsNotEnabled()
        composeTestRule.onAllNodesWithText(LOGOUT)[1].assertIsDisplayed()
        composeTestRule.onAllNodesWithText(LOGOUT)[2].assertHasClickAction().assertIsEnabled()
        composeTestRule.onNodeWithText(WITHDRAW).assertIsNotEnabled()
    }

    @Test
    fun should_remove_dialog_when_clicking_logout_button_in_logout_dialog() {
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(LOGOUT).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(LOGOUT).performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(BACKUP_DIALOG).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onAllNodesWithText(LOGOUT)[0].assertIsNotEnabled()
        composeTestRule.onAllNodesWithText(LOGOUT)[1].assertIsDisplayed()
        composeTestRule.onAllNodesWithText(LOGOUT)[2].assertHasClickAction().assertIsEnabled()
        composeTestRule.onAllNodesWithText(LOGOUT)[2].performClick()
        composeTestRule.waitUntil {
            composeTestRule.onAllNodesWithText(LOGOUT_DIALOG).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onAllNodesWithText(LOGOUT)[0].assertIsNotEnabled()
        composeTestRule.onAllNodesWithText(LOGOUT)[1].assertIsDisplayed()
        composeTestRule.onAllNodesWithText(LOGOUT)[2].assertHasClickAction().assertIsEnabled()
        composeTestRule.onAllNodesWithText(LOGOUT)[2].performClick()
        composeTestRule.waitUntil {
            composeTestRule.onAllNodesWithText(LOGOUT_DIALOG).fetchSemanticsNodes().isEmpty()
        }
        composeTestRule.onNodeWithText(LOGOUT_DIALOG).assertDoesNotExist()
        composeTestRule.onNodeWithText(CANCEL).assertDoesNotExist()
    }

    @Test
    fun should_display_account_settings_screen_when_clicking_cancel_button_in_logout_dialog() {
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(LOGOUT).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(LOGOUT).performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(BACKUP_DIALOG).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onAllNodesWithText(LOGOUT)[0].assertIsNotEnabled()
        composeTestRule.onAllNodesWithText(LOGOUT)[1].assertIsDisplayed()
        composeTestRule.onAllNodesWithText(LOGOUT)[2].assertHasClickAction().assertIsEnabled()
        composeTestRule.onAllNodesWithText(LOGOUT)[2].performClick()
        composeTestRule.onNodeWithText(CANCEL).performClick()
        composeTestRule.waitUntil {
            composeTestRule.onAllNodesWithText(LOGOUT_DIALOG).fetchSemanticsNodes().isEmpty()
        }
        composeTestRule.onNodeWithText(LOGOUT_DIALOG).assertDoesNotExist()
        composeTestRule.onNodeWithText(CANCEL).assertDoesNotExist()
    }

    @Test
    fun should_display_first_withdraw_dialog_when_clicking_withdraw_button() {
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(WITHDRAW).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(WITHDRAW).performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(FIRST_WITHDRAW_DIALOG).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(FIRST_WITHDRAW_DIALOG).assertIsDisplayed()
        composeTestRule.onNodeWithText(CANCEL).assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText(PROCEED).assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText(LOGOUT).assertIsNotEnabled()
        composeTestRule.onAllNodesWithText(WITHDRAW)[0].assertIsNotEnabled()
        composeTestRule.onAllNodesWithText(WITHDRAW)[1].assertIsEnabled()
    }

    @Test
    fun should_display_second_withdraw_dialog_when_clicking_progress_button_in_first_withdraw_dialog() {
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(WITHDRAW).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(WITHDRAW).performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(FIRST_WITHDRAW_DIALOG).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(PROCEED).performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(SECOND_WITHDRAW_DIALOG).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(SECOND_WITHDRAW_DIALOG).assertIsDisplayed()
        composeTestRule.onNodeWithText(CANCEL).assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText(LOGOUT).assertIsNotEnabled()
        composeTestRule.onAllNodesWithText(WITHDRAW)[0].assertIsNotEnabled()
        composeTestRule.onAllNodesWithText(WITHDRAW)[1].assertIsDisplayed()
        composeTestRule.onAllNodesWithText(WITHDRAW)[2].assertHasClickAction().assertIsEnabled()
    }

    @Test
    fun should_remove_dialog_when_clicking_cancel_button_in_first_withdraw_dialog() {
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(WITHDRAW).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(WITHDRAW).performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(FIRST_WITHDRAW_DIALOG).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(CANCEL).performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(FIRST_WITHDRAW_DIALOG).fetchSemanticsNodes().isEmpty()
        }
        composeTestRule.onNodeWithText(FIRST_WITHDRAW_DIALOG).assertDoesNotExist()
        composeTestRule.onNodeWithText(CANCEL).assertDoesNotExist()
        composeTestRule.onNodeWithText(LOGOUT).assertIsEnabled()
        composeTestRule.onNodeWithText(WITHDRAW).assertIsEnabled()
    }

    @Test
    fun should_remove_dialog_when_clicking_withdraw_button_in_second_withdraw_dialog() {
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(WITHDRAW).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(WITHDRAW).performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(FIRST_WITHDRAW_DIALOG).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(PROCEED).performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(SECOND_WITHDRAW_DIALOG).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onAllNodesWithText(WITHDRAW)[0].assertIsNotEnabled()
        composeTestRule.onAllNodesWithText(WITHDRAW)[1].assertIsDisplayed()
        composeTestRule.onAllNodesWithText(WITHDRAW)[2].assertHasClickAction().assertIsEnabled()
        composeTestRule.onAllNodesWithText(WITHDRAW)[2].performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(SECOND_WITHDRAW_DIALOG).fetchSemanticsNodes().isEmpty()
        }
        composeTestRule.onNodeWithText(SECOND_WITHDRAW_DIALOG).assertDoesNotExist()
        composeTestRule.onNodeWithText(CANCEL).assertDoesNotExist()
    }

    @Test
    fun should_display_account_settings_screen_when_clicking_cancel_button_in_second_withdraw_dialog() {
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(WITHDRAW).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(WITHDRAW).performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(FIRST_WITHDRAW_DIALOG).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(PROCEED).performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(SECOND_WITHDRAW_DIALOG).fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(CANCEL).performClick()
        composeTestRule.waitUntil{
            composeTestRule.onAllNodesWithText(SECOND_WITHDRAW_DIALOG).fetchSemanticsNodes().isEmpty()
        }
        composeTestRule.onNodeWithText(SECOND_WITHDRAW_DIALOG).assertDoesNotExist()
        composeTestRule.onNodeWithText(CANCEL).assertDoesNotExist()
    }

    @After
    fun tearDown() {
        composeTestRule.activityRule.scenario.close()
    }

    companion object {
        const val ACCOUNT = "계정"
        const val EMAIL = "이메일"
        const val NICKNAME = "닉네임"
        const val LOGOUT = "로그아웃"
        const val WITHDRAW = "회원탈퇴"
        const val FAKE_EMAIL = "email@email.com"
        const val FAKE_NICKNAME = "nickname"
        const val BACKUP = "백업"
        const val BACKUP_DIALOG = "로그아웃하기 전 백업하시겠습니까? 변경사항을 백업하지 않으면 이용 기록이 손실될 수 있습니다."
        const val LOGOUT_DIALOG = "정말로 로그아웃하시겠습니까?"
        const val CANCEL = "취소"
        const val PROCEED = "진행"
        const val FIRST_WITHDRAW_DIALOG = "회원탈퇴를 하면 지금까지의 이용 기록이 모두 사라지며 복구할 수 없습니다. SpeechBuddy를 다시 이용하시려면 게스트 모드를 이용하거나 회원가입을 새로 해야 합니다. 정말로 탈퇴하시겠습니까?"
        const val SECOND_WITHDRAW_DIALOG = "정말로 탈퇴하시겠습니까? 이 동작은 취소할 수 없습니다."
    }

}