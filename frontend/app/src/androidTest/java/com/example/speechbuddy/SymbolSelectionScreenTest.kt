package com.example.speechbuddy

import android.content.Context
import android.provider.ContactsContract.Data
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
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.room.Database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider

import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters

import com.example.speechbuddy.compose.symbolselection.SymbolSelectionScreen
import com.example.speechbuddy.data.local.AppDatabase
import com.example.speechbuddy.data.local.CategoryDao
import com.example.speechbuddy.data.local.SymbolDao

import com.example.speechbuddy.data.local.WeightRowDao
import com.example.speechbuddy.data.local.models.CategoryMapper
import com.example.speechbuddy.data.local.models.SymbolMapper
import com.example.speechbuddy.data.remote.MySymbolRemoteSource
import com.example.speechbuddy.data.remote.ProxyImageDownloader
import com.example.speechbuddy.data.remote.RealImageDownloader
import com.example.speechbuddy.data.remote.models.MySymbolDtoMapper
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.utils.Converters
import com.example.speechbuddy.repository.SymbolRepository
import com.example.speechbuddy.repository.WeightTableRepository
import com.example.speechbuddy.service.BackupService
import com.example.speechbuddy.service.SymbolCreationService
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.utils.ResponseHandler
import com.example.speechbuddy.viewmodel.SymbolSelectionViewModel
import com.example.speechbuddy.worker.SeedDatabaseWorker
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

import org.junit.After

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@HiltAndroidTest
class SymbolSelectionScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<AuthActivity>()

    // Room database instance
    private lateinit var database: AppDatabase
    private lateinit var symbolDao: SymbolDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var weightRowDao: WeightRowDao

    val retrofit = Retrofit.Builder()
        .baseUrl("https://speechbuddy-1.herokuapp.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val backupService = retrofit.create(BackupService::class.java)
    val symbolCreationService = retrofit.create(SymbolCreationService::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()

        val context = ApplicationProvider.getApplicationContext<Context>()
//        val worker = SeedDatabaseWorker(context, WorkerParameters.DEFAULT)
        val path = context.getDatabasePath("speechbuddy-db")

        database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            path.toString()
        ).build()

//        database = Room.inMemoryDatabaseBuilder(
//            context,
//            AppDatabase::class.java
//        ).build()

        symbolDao = database.symbolDao()
        categoryDao = database.categoryDao()
        weightRowDao = database.weightRowDao()

        runBlocking {
            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
            WorkManager.getInstance(context).enqueue(request)
            Thread.sleep(5000)
        }

        composeTestRule.activity.setContent {
            SpeechBuddyTheme(
                settingsRepository = composeTestRule.activity.settingsRepository,
                initialDarkMode = false
            ) {
                SymbolSelectionScreen(
                    paddingValues = PaddingValues(),
                    topAppBarState = remember{ mutableStateOf(true) },
                    bottomNavBarState = remember{ mutableStateOf(true) },
                    viewModel = SymbolSelectionViewModel(
                        repository = SymbolRepository(
                            symbolDao = symbolDao,
                            categoryDao = categoryDao,
                            mySymbolRemoteSource = MySymbolRemoteSource(
                                symbolCreationService = symbolCreationService,
                                responseHandler = ResponseHandler()
                            ),
                            proxyImageDownloader = ProxyImageDownloader(
                                realImageDownloader = RealImageDownloader(
                                    backupService = backupService,
                                    context = context
                                    //InstrumentationRegistry.getInstrumentation().context
                                ),
                                context = context
                                //InstrumentationRegistry.getInstrumentation().context
                            ),
                            mySymbolDtoMapper = MySymbolDtoMapper(),
                            symbolMapper = SymbolMapper(),
                            categoryMapper = CategoryMapper(),
                            sessionManager = SessionManager(),
                            responseHandler = ResponseHandler()
                        ),
                        weightTableRepository = WeightTableRepository(
                            symbolDao = symbolDao,
                            weightRowDao = weightRowDao,
                            converters = Converters(),
                        ),
                    )
                )
            }
        }
    }

    @After
    fun tearDown(){
        database.close()
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

    }

    @Test
    fun should_change_symbols_displayed_when_each_menu_clicked() {
        composeTestRule.onNodeWithText(ALL_MENU_TEXT).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(TEST_CATEGORY_TEXT).assertIsDisplayed()

        composeTestRule.onNodeWithText(SYMBOL_MENU_TEXT).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(TEST_SYMBOL_TEXT).assertIsDisplayed()

        composeTestRule.onNodeWithText(BIG_CATEGORY_TEXT).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(TEST_CATEGORY_TEXT).assertIsDisplayed()
    }

    @Test
    fun should_display_proper_symbols_when_each_category_clicked() {
//        composeTestRule.onNodeWithText(TEST_CATEGORY_TEXT).performClick()
//        Thread.sleep(3000)
//        composeTestRule.onAllNodesWithText("가족")[0].performClick()
        composeTestRule.onNodeWithText("가족").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("남동생").assertIsDisplayed()
    }

    @Test
    fun should_display_proper_symbols_when_search_box_typed() {
        composeTestRule.onNodeWithText(SEARCH_BOX_TEXT).performTextInput("가")
        //composeTestRule.waitForIdle()
        //composeTestRule.onNodeWithText(DELETE_ALL_BUTTON_TEXT).performClick()
        //composeTestRule.waitForIdle()

        //composeTestRule.onNodeWithText("가게").assertIsDisplayed()
        //composeTestRule.onNodeWithText("가다").assertIsDisplayed()
        //composeTestRule.onNodeWithText("가방").assertIsDisplayed()
    }

    @Test
    fun should_display_proper_symbols_on_selected_symbols_when_symbols_clicked(){
        composeTestRule.onNodeWithText("가족").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("남동생").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(BIG_CATEGORY_TEXT).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("남동생").assertIsDisplayed()
    }

    @Test
    fun should_clear_symbols_on_selected_symbols_when_DELETE_ALL_BUTTON_clicked(){
        composeTestRule.onNodeWithText("가족").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("남동생").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(BIG_CATEGORY_TEXT).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("남동생").assertIsDisplayed()
    }

    @Test
    fun should_display_in_SEE_BIG_mode_when_SEE_BIG_BUTTON_clicked(){
        composeTestRule.onNodeWithText("가족").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("남동생").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(SEE_BIG_BUTTON_TEXT).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(EXIT_BUTTON_TEXT).assertIsDisplayed()

    }


    //


    companion object {
        //const val SEARCH_BOX_TEXT = "SymbolSearchTextField"
        const val SEARCH_BOX_TEXT = "검색어를 입력하세요"
        const val SEE_BIG_BUTTON_TEXT = "크게 보기"
        const val DELETE_ALL_BUTTON_TEXT = "모두 삭제"
        const val ALL_MENU_TEXT = "전체"
        const val SYMBOL_MENU_TEXT = "상징"
        const val BIG_CATEGORY_TEXT = "대분류"
        const val FAVORITE_MENU_TEXT = "즐겨찾기"
        const val TEST_CATEGORY_TEXT = "가족"
        const val TEST_SYMBOL_TEXT = "119에 전화해주세요"
        const val EXIT_BUTTON_TEXT = "나가기"
    }

}