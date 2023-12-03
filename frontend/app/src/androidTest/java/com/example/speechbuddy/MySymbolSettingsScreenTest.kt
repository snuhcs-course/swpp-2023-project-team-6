package com.example.speechbuddy

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.example.speechbuddy.compose.emailverification.EmailVerificationScreen
import com.example.speechbuddy.compose.settings.MySymbolSettings
import com.example.speechbuddy.compose.symbolcreation.SymbolCreationScreen
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest

import org.junit.Before
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
class MySymbolSettingsScreenTest {

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
                MySymbolSettings(
                    paddingValues = PaddingValues()
                )
            }
        }
    }

    @Test
    fun should_display_all_elements_when_mysybolsettings_screen_with_my_symbol_menu_appears() {
        composeTestRule.onNodeWithText(SEARCH_BOX_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(MY_SYMBOL_MENU_TEXT).assertIsDisplayed()

        composeTestRule.onNodeWithText(FAVORITES_MENU_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(DELETE_TEXT).assertIsDisplayed()
    }

    @Test
    fun should_display_all_elements_when_mysybolsettings_screen_with_favorite_menu_appears() {
        composeTestRule.onNodeWithText(FAVORITES_MENU_TEXT).performClick()
        composeTestRule.onNodeWithText(SEARCH_BOX_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(MY_SYMBOL_MENU_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(FAVORITES_MENU_TEXT).assertIsDisplayed()
        //composeTestRule.onNodeWithText(DELETE_TEXT).assertIsNotDisplayed()
    }


    //

    companion object {
        const val SEARCH_BOX_TEXT = "검색어를 입력하세요"
        const val MY_SYMBOL_MENU_TEXT = "내가 만든 상징"
        const val FAVORITES_MENU_TEXT = "즐겨찾기"
        const val DELETE_TEXT = "삭제"

    }

}