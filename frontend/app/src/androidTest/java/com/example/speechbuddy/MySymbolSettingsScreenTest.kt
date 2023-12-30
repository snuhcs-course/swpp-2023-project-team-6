package com.example.speechbuddy

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.speechbuddy.compose.settings.MySymbolSettings
import com.example.speechbuddy.data.local.AppDatabase
import com.example.speechbuddy.data.local.CategoryDao
import com.example.speechbuddy.data.local.SymbolDao
import com.example.speechbuddy.data.local.WeightRowDao
import com.example.speechbuddy.data.local.models.CategoryMapper
import com.example.speechbuddy.data.local.models.SymbolEntity
import com.example.speechbuddy.data.local.models.SymbolMapper
import com.example.speechbuddy.data.remote.MySymbolRemoteSource
import com.example.speechbuddy.data.remote.ProxyImageDownloader
import com.example.speechbuddy.data.remote.RealImageDownloader
import com.example.speechbuddy.data.remote.RemoveImage
import com.example.speechbuddy.data.remote.models.MySymbolDtoMapper
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.utils.Converters
import com.example.speechbuddy.repository.SymbolRepository
import com.example.speechbuddy.repository.WeightTableRepository
import com.example.speechbuddy.service.BackupService
import com.example.speechbuddy.service.SymbolCreationService
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.utils.ResponseHandler
import com.example.speechbuddy.viewmodel.MySymbolSettingsViewModel
import com.example.speechbuddy.worker.SeedDatabaseWorker
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@HiltAndroidTest
class MySymbolSettingsScreenTest {

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

        symbolDao = database.symbolDao()
        categoryDao = database.categoryDao()
        weightRowDao = database.weightRowDao()

        runBlocking {
            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
            WorkManager.getInstance(context).enqueue(request)
            Thread.sleep(5000)

            val symbolEntity = symbolDao.getSymbolById(1)
            val symbol = SymbolMapper().mapToDomainModel(symbolEntity.first())
            symbolDao.updateSymbol(
                SymbolMapper().mapFromDomainModel(
                    symbol.copy(
                        isMine = false,
                        isFavorite = true
                    )
                )
            )
            symbolDao.insertSymbol(
                SymbolEntity(
                    id = 1000,
                    text = "test",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = false,
                    isMine = true
                )
            )
        }

        composeTestRule.activity.setContent {
            SpeechBuddyTheme(
                settingsRepository = composeTestRule.activity.settingsRepository,
                initialDarkMode = false
            ) {
                MySymbolSettings(
                    paddingValues = PaddingValues(),
                    viewModel = MySymbolSettingsViewModel(
                        weightTableRepository = WeightTableRepository(
                            symbolDao = symbolDao,
                            weightRowDao = weightRowDao,
                            converters = Converters(),
                        ),
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
                            responseHandler = ResponseHandler(),
                            removeImage = RemoveImage(context)
                        ),
                    )
                )
            }
        }
    }

    @Test
    fun should_display_all_elements_when_mysybolsettings_screen_appears() {
        composeTestRule.onNodeWithText(SEARCH_BOX_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(MY_SYMBOL_MENU_TEXT).assertIsDisplayed()

        composeTestRule.onNodeWithText(FAVORITES_MENU_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(DELETE_TEXT).assertIsDisplayed()
    }

    @Test
    fun should_display_proper_symbols_when_in_MY_SYMBOL_MODE() {
        composeTestRule.onNodeWithText(TEST_SYMBOL_TEXT_FOR_CREATION).assertIsDisplayed()
    }

    @Test
    fun should_display_proper_symbols_when_in_FAVORITES_MODE() {
        composeTestRule.onNodeWithText(FAVORITES_MENU_TEXT).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(TEST_SYMBOL_TEXT_FOR_FAVORITE).assertIsDisplayed()
    }


    //

    companion object {
        const val SEARCH_BOX_TEXT = "검색어를 입력하세요"
        const val MY_SYMBOL_MENU_TEXT = "내가 만든 상징"
        const val FAVORITES_MENU_TEXT = "즐겨찾기"
        const val DELETE_TEXT = "삭제"
        const val TEST_SYMBOL_TEXT_FOR_CREATION = "test"
        const val TEST_SYMBOL_TEXT_FOR_FAVORITE = "119에 전화해주세요"

    }

}