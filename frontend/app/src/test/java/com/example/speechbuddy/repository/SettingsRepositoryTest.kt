package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.SettingsPrefsManager
import com.example.speechbuddy.data.local.models.WeightRowEntity
import com.example.speechbuddy.data.remote.SettingsRemoteSource
import com.example.speechbuddy.data.remote.models.FavoritesListDto
import com.example.speechbuddy.data.remote.models.SettingsBackupDto
import com.example.speechbuddy.data.remote.models.SymbolDto
import com.example.speechbuddy.data.remote.models.SymbolIdDto
import com.example.speechbuddy.data.remote.models.SymbolListDto
import com.example.speechbuddy.data.remote.requests.BackupWeightTableRequest
import com.example.speechbuddy.data.remote.requests.WeightTableEntity
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.SettingsPreferences
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.domain.utils.Converters
import com.example.speechbuddy.service.BackupService
import com.example.speechbuddy.ui.models.InitialPage
import com.example.speechbuddy.utils.ResponseHandler
import com.example.speechbuddy.utils.Status
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class SettingsRepositoryTest {

    private lateinit var settingsRepository: SettingsRepository

    @MockK
    private val settingsPrefsManager = mockk<SettingsPrefsManager>()
    private val backupService = mockk<BackupService>()
    private val responseHandler = ResponseHandler()
    private val sessionManager = mockk<SessionManager>()
    private val symbolRepository = mockk<SymbolRepository>()
    private val weightTableRepository = mockk<WeightTableRepository>()
    private val settingsRemoteSource = mockk<SettingsRemoteSource>()
    private val converters = Converters()

    private val mockAccessToken = "testAccessToken"
    private val mockAuthHeader = "Bearer $mockAccessToken"

    private val mockErrorJson = """
    {
        "error": {
            "code": 000,
            "message": {
                "key of message": [
                    "error description"
                ]
            }
        }
    }
    """
    private val mockErrorResponseBody =
        mockErrorJson.toResponseBody("application/json".toMediaType())

    @Before
    fun setUp() {
        settingsRepository = SettingsRepository(
            settingsPrefsManager,
            backupService,
            responseHandler,
            sessionManager,
            symbolRepository,
            weightTableRepository,
            settingsRemoteSource,
            converters
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should save dark mode when setDarkMode is called with true`() = runBlocking {
        val darkModeValue = true
        coEvery { settingsPrefsManager.saveDarkMode(any()) } returns Unit

        settingsRepository.setDarkMode(darkModeValue)

        coVerify { settingsPrefsManager.saveDarkMode(true) }
    }

    @Test
    fun `should save light mode when setDarkMode is called with false`() = runBlocking {
        val darkModeValue = false
        coEvery { settingsPrefsManager.saveDarkMode(any()) } returns Unit

        settingsRepository.setDarkMode(darkModeValue)

        coVerify { settingsPrefsManager.saveDarkMode(false) }
    }

    @Test
    fun `should save initial page as true when setInitialPage is called with SYMBOL_SELECTION`() = runBlocking {
        val initialPage = InitialPage.SYMBOL_SELECTION
        coEvery { settingsPrefsManager.saveInitialPage(any()) } returns Unit

        settingsRepository.setInitialPage(initialPage)

        coVerify { settingsPrefsManager.saveInitialPage(true) }
    }

    @Test
    fun `should save initial page as false when setInitialPage is called with TTS`() = runBlocking {
        val initialPage = InitialPage.TEXT_TO_SPEECH
        coEvery { settingsPrefsManager.saveInitialPage(any()) } returns Unit

        settingsRepository.setInitialPage(initialPage)

        coVerify { settingsPrefsManager.saveInitialPage(false) }
    }

    @Test
    fun `should save autoBackup when AutoBackup is called`() = runBlocking {
        val autoBackupValue = true
        coEvery { settingsPrefsManager.saveAutoBackup(any()) } returns Unit

        settingsRepository.setAutoBackup(autoBackupValue)

        coVerify { settingsPrefsManager.saveAutoBackup(autoBackupValue) }
    }

    @Test
    fun `should save lastBackupDate when setLastBackupDate is called`() = runBlocking {
        val lastBackupDate = "2023-01-01"
        coEvery { settingsPrefsManager.saveLastBackupDate(any()) } returns Unit

        settingsRepository.setLastBackupDate(lastBackupDate)

        coVerify { settingsPrefsManager.saveLastBackupDate(lastBackupDate) }
    }

    @Test
    fun `should return dark mode preference when getDarkMode is called`() = runBlocking {
        val darkModeValue = true
        coEvery { settingsPrefsManager.settingsPreferencesFlow } returns flowOf(SettingsPreferences(darkMode = darkModeValue))

        val result = settingsRepository.getDarkMode()

        result.collect { resource ->
            assert(resource.status == Status.SUCCESS)
            assert(resource.data == darkModeValue)
            assert(resource.message == null)
        }
    }

    @Test
    fun `should return dark mode boolean value when getDarkModeForChange is called`() = runBlocking {
        val darkModeValue = true
        coEvery { settingsPrefsManager.settingsPreferencesFlow } returns flowOf(
            SettingsPreferences(
                darkMode = darkModeValue
            )
        )

        val result = settingsRepository.getDarkModeForChange()

        result.collect { resource ->
            assert(resource == darkModeValue)
        }
    }

    @Test
    fun `should return initial page preference when getInitialPage is called`() = runBlocking {
        val initialPageValue = true
        coEvery { settingsPrefsManager.settingsPreferencesFlow } returns flowOf(SettingsPreferences(initialPage = initialPageValue))

        val result = settingsRepository.getInitialPage()

        result.collect { resource ->
            assert(resource.status == Status.SUCCESS)
            assert(resource.data == initialPageValue)
            assert(resource.message == null)
        }
    }

    @Test
    fun `should return auto backup preference when getAutoBackup is called`() = runBlocking {
        val autoBackupValue = true
        coEvery { settingsPrefsManager.settingsPreferencesFlow } returns flowOf(SettingsPreferences(autoBackup = autoBackupValue))

        val result = settingsRepository.getAutoBackup()

        result.collect { resource ->
            assert(resource.status == Status.SUCCESS)
            assert(resource.data == autoBackupValue)
            assert(resource.message == null)
        }
    }

    @Test
    fun `should return last backup date preference when getLastBackupDate is called`() = runBlocking {
        val lastBackupDateValue = "2023-01-01"
        coEvery { settingsPrefsManager.settingsPreferencesFlow } returns flowOf(SettingsPreferences(lastBackupDate = lastBackupDateValue))

        val result = settingsRepository.getLastBackupDate()

        result.collect { resource ->
            assert(resource.status == Status.SUCCESS)
            assert(resource.data == lastBackupDateValue)
            assert(resource.message == null)
        }
    }

    @Test
    fun `should reset settings when resetSettings is called`() = runBlocking {
        coEvery { settingsPrefsManager.resetSettings() } returns Unit
        settingsRepository.resetSettings()
        coVerify { settingsPrefsManager.resetSettings() }
    }

    @Test
    fun `should return error response when displayBackup fails`() = runBlocking {
        val mockDarkMode = 1
        val mockInitialPage = 1

        val settingsBackupDto = SettingsBackupDto(
            mockDarkMode,
            mockInitialPage
        )

        coEvery { sessionManager.cachedToken.value?.accessToken } returns mockAccessToken
        coEvery { backupService.displayBackup(mockAuthHeader, settingsBackupDto) } returns responseHandler.getConnectionErrorResponse()

        val result = settingsRepository.displayBackup()

        result.collect { resource ->
            assert(!resource.isSuccessful)
            assert(resource.code() == 600)
        }
    }

    @Test
    fun `should return successful response when symbolListBackup with no Ids is successful`() = runBlocking {
        val successResponse = Response.success<Void>(200, null)

        coEvery { sessionManager.cachedToken.value?.accessToken } returns mockAccessToken
        coEvery { symbolRepository.getUserSymbolsIdString() } returns ""
        coEvery { backupService.symbolListBackup(header = mockAuthHeader) } returns successResponse

        val result = settingsRepository.symbolListBackup()

        result.collect { resource ->
            assert(resource.isSuccessful)
            assert(resource.code() == 200)
            assert(resource.body() == null)
        }
    }

    @Test
    fun `should return error response when symbolListBackup fails`() = runBlocking {

        coEvery { sessionManager.cachedToken.value?.accessToken } returns mockAccessToken
        coEvery { backupService.symbolListBackup(header = mockAuthHeader) } returns responseHandler.getConnectionErrorResponse()

        val result = settingsRepository.symbolListBackup()

        result.collect { resource ->
            assert(!resource.isSuccessful)
            assert(resource.code() == 600)
        }
    }

    @Test
    fun `should return successful response when symbolListBackup with Ids is successful`() = runBlocking {
        val mockQueryIds = "1,2,3"
        val successResponse = Response.success<Void>(200, null)

        coEvery { sessionManager.cachedToken.value?.accessToken } returns mockAccessToken
        coEvery { symbolRepository.getUserSymbolsIdString() } returns mockQueryIds
        coEvery { backupService.symbolListBackup(mockQueryIds, mockAuthHeader) } returns successResponse

        val result = settingsRepository.symbolListBackup()

        result.collect { resource ->
            assert(resource.isSuccessful)
            assert(resource.code() == 200)
            assert(resource.body() == null)
        }
    }

    @Test
    fun `should return successful response when favoriteSymbolBackup with no Ids is successful`() = runBlocking {
        val successResponse = Response.success<Void>(200, null)

        coEvery { sessionManager.cachedToken.value?.accessToken } returns mockAccessToken
        coEvery { symbolRepository.getFavoriteSymbolsIdString() } returns ""
        coEvery { backupService.favoriteSymbolBackup(header = mockAuthHeader) } returns successResponse

        val result = settingsRepository.favoriteSymbolBackup()

        result.collect { resource ->
            assert(resource.isSuccessful)
            assert(resource.code() == 200)
            assert(resource.body() == null)
        }
    }

    @Test
    fun `should return successful response when favoriteSymbolBackup with Ids is successful`() = runBlocking {
        val mockQueryIds = "1,2,3"
        val successResponse = Response.success<Void>(200, null)

        coEvery { sessionManager.cachedToken.value?.accessToken } returns mockAccessToken
        coEvery { symbolRepository.getFavoriteSymbolsIdString() } returns mockQueryIds
        coEvery { backupService.favoriteSymbolBackup(mockQueryIds, mockAuthHeader) } returns successResponse

        val result = settingsRepository.favoriteSymbolBackup()

        result.collect { resource ->
            assert(resource.isSuccessful)
            assert(resource.code() == 200)
            assert(resource.body() == null)
        }
    }

    @Test
    fun `should return successful response when weightTableBackup is successful`() = runBlocking {
        val weightRowEntities = mutableListOf<WeightRowEntity>()
        for (i in 1..500) {
            val tmpObj = WeightRowEntity(
                id = i,
                weights = List(500) { 0 }
            )
            weightRowEntities.add(tmpObj)
        }
        val weightTableEntities = weightRowEntities.map { weightRow ->
            WeightTableEntity(
                id = weightRow.id,
                weight = converters.fromList(weightRow.weights)
            )
        }
        val backupWeightTableRequest = BackupWeightTableRequest(weightTableEntities)

        val successResponse = Response.success<Void>(200, null)

        coEvery { sessionManager.cachedToken.value?.accessToken } returns mockAccessToken
        coEvery { weightTableRepository.getBackupWeightTableRequest() } returns backupWeightTableRequest
        coEvery { backupService.weightTableBackup(header = mockAuthHeader, backupWeightTableRequest) } returns successResponse

        val result = settingsRepository.weightTableBackup()

        result.collect { resource ->
            assert(resource.isSuccessful)
            assert(resource.code() == 200)
            assert(resource.body() == null)
        }
    }


    @Test
    fun `should return success resource when getDisplaySettingsFromRemote is successful`() = runBlocking {
        val mockSettingsDto = SettingsBackupDto(
            displayMode = 1,
            defaultMenu = 1,
            updatedAt = "2023-01-01T12:00:00Z"
        )

        val successResponse = Response.success<SettingsBackupDto>(200, mockSettingsDto)
        coEvery { settingsRemoteSource.getDisplaySettings(any()) } returns flowOf(successResponse)
        coEvery { settingsPrefsManager.saveLastBackupDate(any()) } returns Unit
        coEvery { settingsPrefsManager.saveInitialPage(any()) } returns Unit
        coEvery { settingsPrefsManager.saveDarkMode(any()) } returns Unit

        val result = settingsRepository.getDisplaySettingsFromRemote(mockAccessToken)
        result.collect { resource ->
            assert(resource.status == Status.SUCCESS)
            assert(resource.data == null)
            assert(resource.message == null)
        }
    }

    @Test
    fun `should return error resource when getDisplaySettingsFromRemote fails`() = runBlocking {
        val errorResponse = Response.error<SettingsBackupDto>(400, mockErrorResponseBody)
        coEvery { settingsRemoteSource.getDisplaySettings(any()) } returns flowOf(errorResponse)

        val result = settingsRepository.getDisplaySettingsFromRemote(mockAccessToken)
        result.collect { resource ->
            assert(resource.status == Status.ERROR)
            assert(resource.data == null)
            assert(resource.message == "key of message")
        }
    }

    @Test
    fun `should return success resource when getSymbolListFromRemote is successful`() = runBlocking {
        val mockSymbolDto = SymbolDto(
            id = 1,
            text = "example",
            image = "example_image_url",
            category = 1,
            createdAt = "2023-01-01T12:00:00Z"
        )
        val mockSymbolListDto = SymbolListDto(listOf(mockSymbolDto))
        val successResponse = Response.success<SymbolListDto>(200, mockSymbolListDto)

        coEvery { settingsRemoteSource.getSymbolList(any()) } returns flowOf(successResponse)
        coEvery { symbolRepository.insertSymbol(any()) } returns Unit

        val result = settingsRepository.getSymbolListFromRemote(mockAccessToken)

        result.collect { resource ->
            assert(resource.status == Status.SUCCESS)
            assert(resource.data == null)
            assert(resource.message == null)
        }
    }

    @Test
    fun `should return error resource when getSymbolListFromRemote fails`() = runBlocking {
        val errorResponse = Response.error<SymbolListDto>(400, mockErrorResponseBody)

        coEvery { settingsRemoteSource.getSymbolList(any()) } returns flowOf(errorResponse)

        val result = settingsRepository.getSymbolListFromRemote(mockAccessToken)

        result.collect { resource ->
            assert(resource.status == Status.ERROR)
            assert(resource.data == null)
            assert(resource.message == "key of message")
        }
    }

    @Test
    fun `should return success resource when getFavoritesListFromRemote is successful`() = runBlocking {
        val mockSymbolIdDto = SymbolIdDto(1)
        val mockFavSymbol = Symbol(
            id = 1,
            text = "example",
            imageUrl = "example_image_url",
            categoryId = 1,
            isFavorite = false,
            isMine = false
        )
        val mockFavoritesListDto = FavoritesListDto(listOf(mockSymbolIdDto))
        val successResponse = Response.success<FavoritesListDto>(200, mockFavoritesListDto)

        coEvery { settingsRemoteSource.getFavoritesList(any()) } returns flowOf(successResponse)
        coEvery { symbolRepository.getSymbolsById(mockSymbolIdDto.id) } returns mockFavSymbol
        coEvery { symbolRepository.updateFavorite(mockFavSymbol, true) } returns Unit

        val result = settingsRepository.getFavoritesListFromRemote(mockAccessToken)

        result.collect { resource ->
            assert(resource.status == Status.SUCCESS)
            assert(resource.data == null)
            assert(resource.message == null)
        }
    }

    @Test
    fun `should return error resource when getFavoritesListFromRemote fails`() = runBlocking {
        val errorResponse = Response.error<FavoritesListDto>(400, mockErrorResponseBody)

        coEvery { settingsRemoteSource.getFavoritesList(any()) } returns flowOf(errorResponse)

        val result = settingsRepository.getFavoritesListFromRemote(mockAccessToken)

        result.collect { resource ->
            assert(resource.status == Status.ERROR)
            assert(resource.data == null)
            assert(resource.message == "key of message")
        }
    }

    @Test
    fun `should return success resource when getWeightTableFromRemote is successful`() = runBlocking {
        val weightRowEntities = mutableListOf<WeightRowEntity>()
        for (i in 1..500) {
            val tmpObj = WeightRowEntity(
                id = i,
                weights = List(500) { 0 }
            )
            weightRowEntities.add(tmpObj)
        }
        val weightTableEntities = weightRowEntities.map { weightRow ->
            WeightTableEntity(
                id = weightRow.id,
                weight = converters.fromList(weightRow.weights)
            )
        }
        val mockBackupWeightTableRequest : BackupWeightTableRequest? = BackupWeightTableRequest(weightTableEntities)
        val successResponse = Response.success<BackupWeightTableRequest>(200, mockBackupWeightTableRequest)

        coEvery { settingsRemoteSource.getWeightTable(any()) } returns flowOf(successResponse)
        coEvery { weightTableRepository.replaceWeightTable(any()) } returns Unit

        val result = settingsRepository.getWeightTableFromRemote(mockAccessToken)

        result.collect { resource ->
            assert(resource.status == Status.SUCCESS)
            assert(resource.data == null)
            assert(resource.message == null)
        }
    }

    @Test
    fun `should return error resource when getWeightTableFromRemote fails`() = runBlocking {
        val errorResponse = Response.error<BackupWeightTableRequest>(400, mockErrorResponseBody)

        coEvery { settingsRemoteSource.getWeightTable(any()) } returns flowOf(errorResponse)

        val result = settingsRepository.getWeightTableFromRemote(mockAccessToken)

        result.collect { resource ->
            assert(resource.status == Status.ERROR)
            assert(resource.data == null)
            assert(resource.message == "key of message")
        }
    }

}