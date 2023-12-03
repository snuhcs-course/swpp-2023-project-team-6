package com.example.speechbuddy

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
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
import com.example.speechbuddy.compose.symbolselection.SymbolSelectionScreen
import com.example.speechbuddy.ui.SpeechBuddyTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest

import org.junit.Before
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
class SymbolSelectionScreenTest {

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
                SymbolSelectionScreen(
                    paddingValues = PaddingValues(),
                    topAppBarState = remember{ mutableStateOf(true) },
                    bottomNavBarState = remember{ mutableStateOf(true) }
                )
            }
        }
    }

    @Test
    fun should_display_all_elements_when_mysybolsettings_screen_with_big_category_menu_appears() {
        composeTestRule.onNodeWithText(SEARCH_BOX_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(SEE_BIG_BUTTON_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(DELETE_ALL_BUTTON_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(ALL_MENU_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(SYMBOL_MENU_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(BIG_CATEGORY_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(FAVORITE_MENU_TEXT).assertIsDisplayed()
        //composeTestRule.onNodeWithText(ALL_MENU_TEXT).assertIsFocused()
    }

    @Test
    fun should_change_symbols_displayed_when_each_menu_clicked() {
        composeTestRule.onNodeWithText(ALL_MENU_TEXT).performClick()
        Thread.sleep(10000)
        //composeTestRule.onNodeWithText(TEST_CATEGORY_TEXT).assertIsDisplayed()
        //composeTestRule.onNodeWithText(TEST_SYMBOL_TEXT).assertIsDisplayed()

    }



    //

    companion object {
        const val SEARCH_BOX_TEXT = "검색어를 입력하세요"
        const val SEE_BIG_BUTTON_TEXT = "크게 보기"
        const val DELETE_ALL_BUTTON_TEXT = "모두 삭제"
        const val ALL_MENU_TEXT = "전체"
        const val SYMBOL_MENU_TEXT = "상징"
        const val BIG_CATEGORY_TEXT = "대분류"
        const val FAVORITE_MENU_TEXT = "즐겨찾기"
        const val TEST_CATEGORY_TEXT = "가족"
        const val TEST_SYMBOL_TEXT = "119에 전화해주세요"
    }

}