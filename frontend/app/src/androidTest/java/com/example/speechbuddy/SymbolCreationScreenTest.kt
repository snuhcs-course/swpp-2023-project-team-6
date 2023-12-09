package com.example.speechbuddy

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.speechbuddy.compose.symbolcreation.SymbolCreationScreen
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
import com.example.speechbuddy.viewmodel.SymbolCreationViewModel
import com.example.speechbuddy.worker.SeedDatabaseWorker
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@HiltAndroidTest
class SymbolCreationScreenTest {

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

        val path = context.getDatabasePath("speechbuddy-db")

        database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            path.toString()
        ).build()

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
                SymbolCreationScreen(
                    paddingValues = PaddingValues(),
                    viewModel = SymbolCreationViewModel(
                        symbolRepository = SymbolRepository(
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

                                ),
                                context = context

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
                        sessionManager = SessionManager()
                    )
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
    fun should_display_alertdialog_with_three_options_when_symbolcreation_clicked() {
        composeTestRule.onNodeWithContentDescription(PHOTO_ICON_DESCRIPTION).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(ALERT_MESSAGE).assertIsDisplayed()
        composeTestRule.onNodeWithText(TAKE_PICTURE).assertIsDisplayed()
        composeTestRule.onNodeWithText(SELECT_FROM_EXISTING).assertIsDisplayed()
        composeTestRule.onNodeWithText(CANCEL).assertIsDisplayed()
    }

    @Test
    fun should_display_all_categories_when_category_selection_is_clicked() {
        composeTestRule.onNodeWithText(BIG_CATEGORY_BOX_TEXT).performClick()
        composeTestRule.waitForIdle()
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