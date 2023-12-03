package com.example.speechbuddy

import android.app.Activity
import android.app.Instrumentation
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.example.speechbuddy.compose.emailverification.EmailVerificationScreen
import com.example.speechbuddy.compose.symbolcreation.SymbolCreationScreen
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@HiltAndroidTest
class SymbolCreationScreenTest {

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
                SymbolCreationScreen(
                    paddingValues = PaddingValues()
                )
            }
        }
    }

    @Test
    fun should_display_all_elements_when_symbolcreation_screen_appears() {
        composeTestRule.onNodeWithText(CREATE_NEW_SYMBOL).assertIsDisplayed()
        composeTestRule.onNodeWithText(CREATE_NEW_SYMBOL_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(PHOTO_ICON_DESCRIPTION).assertIsDisplayed()
        composeTestRule.onNodeWithText(BIG_CATEGORY_BOX_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(SYMBOL_NAME_BOX_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(MAKE_BUTTON_TEXT).assertIsDisplayed()
    }

    @Test
    fun should_display_alertdialog_with_three_options_when_symbolcreation_clicked(){
        composeTestRule.onNodeWithContentDescription(PHOTO_ICON_DESCRIPTION).performClick()
        Thread.sleep(1000)
        composeTestRule.onNodeWithText(ALERT_MESSAGE).assertIsDisplayed()
        composeTestRule.onNodeWithText(TAKE_PICTURE).assertIsDisplayed()
        composeTestRule.onNodeWithText(SELECT_FROM_EXISTING).assertIsDisplayed()
        composeTestRule.onNodeWithText(CANCEL).assertIsDisplayed()
    }
    @Test
    fun should_display_all_categories_when_category_selection_is_clicked(){
        composeTestRule.onNodeWithText(BIG_CATEGORY_BOX_TEXT).performClick()
        Thread.sleep(1000)
        composeTestRule.onNodeWithText("가족").assertIsDisplayed()
    }

    //

    companion object {
        const val CREATE_NEW_SYMBOL = "새 상징 만들기"
        const val CREATE_NEW_SYMBOL_TEXT = "직접 찍은 사진으로 새로운 상징을 만들어보세요"
        const val BIG_CATEGORY_BOX_TEXT = "대분류"
        const val SYMBOL_NAME_BOX_TEXT = "상징 이름을 입력해주세요"
        const val MAKE_BUTTON_TEXT = "만들기"
        const val PHOTO_ICON_DESCRIPTION = "새 상징 만들기"
        const val ALERT_MESSAGE = "사진을 어떻게 추가하실 건가요?"
        const val TAKE_PICTURE = "사진 촬영하기"
        const val SELECT_FROM_EXISTING = "사진 보관함에서 사진 선택하기"
        const val CANCEL = "취소"
    }

}