package com.example.speechbuddy.data.remote

import com.example.speechbuddy.data.remote.models.FavoritesListDto
import com.example.speechbuddy.data.remote.models.SettingsBackupDto
import com.example.speechbuddy.data.remote.models.SymbolListDto
import com.example.speechbuddy.data.remote.requests.BackupWeightTableRequest
import com.example.speechbuddy.service.BackupService
import com.example.speechbuddy.utils.ResponseHandler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class SettingsRemoteSourceTest {

    private lateinit var backupService: BackupService
    private lateinit var settingsRemoteSource: SettingsRemoteSource
    private val responseHandler = ResponseHandler()

    private val mockAccessToken = "testAccessToken"
    private val mockAuthHeader = "Bearer $mockAccessToken"

    private val internetErrorResponseBody =
        "{\"code\": 600, \"message\": \"No Internet Connection\"}".toResponseBody()

    private val errorResponseBody =
        "{\"error\":\"Something went wrong\"}".toResponseBody()

    @Before
    fun setUp() {
        backupService = mockk()
        settingsRemoteSource = SettingsRemoteSource(backupService, responseHandler)
    }

    @Test
    fun `should return SettingsBackupDto when request for display settings is valid`() = runBlocking {
        val expectedResponse: Response<SettingsBackupDto> = mockk(relaxed = true)
        coEvery { backupService.getDisplaySettings(mockAuthHeader) } returns expectedResponse

        settingsRemoteSource.getDisplaySettings(mockAuthHeader).collect { result ->
            assertEquals(expectedResponse, result)
        }
        coVerify(exactly = 1) { backupService.getDisplaySettings(mockAuthHeader) }
    }

    @Test
    fun `should return SymbolListDto when request for symbol list is valid`() = runBlocking {
        val expectedResponse: Response<SymbolListDto> = mockk(relaxed = true)
        coEvery { backupService.getSymbolList(mockAuthHeader) } returns expectedResponse

        settingsRemoteSource.getSymbolList(mockAuthHeader).collect { result ->
            assertEquals(expectedResponse, result)
        }
        coVerify(exactly = 1) { backupService.getSymbolList(mockAuthHeader) }
    }

    @Test
    fun `should return FavoritesListDto when request for favorites list is valid`() = runBlocking {
        val expectedResponse: Response<FavoritesListDto> = mockk(relaxed = true)
        coEvery { backupService.getFavoriteSymbolList(mockAuthHeader) } returns expectedResponse

        settingsRemoteSource.getFavoritesList(mockAuthHeader).collect { result ->
            assertEquals(expectedResponse, result)
        }
        coVerify(exactly = 1) { backupService.getFavoriteSymbolList(mockAuthHeader) }
    }

    @Test
    fun `should return BackupWeightTableRequest when request for weight table is valid`() = runBlocking {
        val expectedResponse: Response<BackupWeightTableRequest> = mockk(relaxed = true)
        coEvery { backupService.getWeightTable(mockAuthHeader) } returns expectedResponse

        settingsRemoteSource.getWeightTable(mockAuthHeader).collect { result ->
            assertEquals(expectedResponse, result)
        }
        coVerify(exactly = 1) { backupService.getWeightTable(mockAuthHeader) }
    }

    @Test
    fun `should return error when request for display settings is invalid`() = runBlocking {
        val expectedResponse = Response.error<SettingsBackupDto>(400, errorResponseBody)
        coEvery { backupService.getDisplaySettings(mockAuthHeader) } returns expectedResponse

        settingsRemoteSource.getDisplaySettings(mockAuthHeader).collect { result ->
            assertEquals(expectedResponse, result)
        }
        coVerify(exactly = 1) { backupService.getDisplaySettings(mockAuthHeader) }
    }

    @Test
    fun `should return error when request for symbol list is invalid`() = runBlocking {
        val expectedResponse = Response.error<SymbolListDto>(400, errorResponseBody)
        coEvery { backupService.getSymbolList(mockAuthHeader) } returns expectedResponse

        settingsRemoteSource.getSymbolList(mockAuthHeader).collect { result ->
            assertEquals(expectedResponse, result)
        }
        coVerify(exactly = 1) { backupService.getSymbolList(mockAuthHeader) }
    }

    @Test
    fun `should return error when request for favorites list is invalid`() = runBlocking {
        val expectedResponse = Response.error<FavoritesListDto>(400, errorResponseBody)
        coEvery { backupService.getFavoriteSymbolList(mockAuthHeader) } returns expectedResponse

        settingsRemoteSource.getFavoritesList(mockAuthHeader).collect { result ->
            assertEquals(expectedResponse, result)
        }
        coVerify(exactly = 1) { backupService.getFavoriteSymbolList(mockAuthHeader) }
    }

    @Test
    fun `should return error when request for weight table is invalid`() = runBlocking {
        val expectedResponse = Response.error<BackupWeightTableRequest>(400, errorResponseBody)
        coEvery { backupService.getWeightTable(mockAuthHeader) } returns expectedResponse

        settingsRemoteSource.getWeightTable(mockAuthHeader).collect { result ->
            assertEquals(expectedResponse, result)
        }
        coVerify(exactly = 1) { backupService.getWeightTable(mockAuthHeader) }
    }

    @Test
    fun `should return internet error when exception happens for symbol list`() = runBlocking {
        val errorResponse = Response.error<SymbolListDto>(600, internetErrorResponseBody)
        coEvery { backupService.getSymbolList(mockAuthHeader) } throws Exception()

        settingsRemoteSource.getSymbolList(mockAuthHeader).collect { result ->
            assertEquals(errorResponse.code(), result.code())
        }
        coVerify(exactly = 1) { backupService.getSymbolList(mockAuthHeader) }
    }

    @Test
    fun `should return internet error when exception happens for display settings`() = runBlocking {
        val errorResponse = Response.error<SettingsBackupDto>(600, internetErrorResponseBody)
        coEvery { backupService.getDisplaySettings(mockAuthHeader) } throws Exception()

        settingsRemoteSource.getDisplaySettings(mockAuthHeader).collect { result ->
            assertEquals(errorResponse.code(), result.code())
        }
        coVerify(exactly = 1) { backupService.getDisplaySettings(mockAuthHeader) }
    }

    @Test
    fun `should return internet error when exception happens for favorites list`() = runBlocking {
        val errorResponse = Response.error<FavoritesListDto>(600, internetErrorResponseBody)
        coEvery { backupService.getFavoriteSymbolList(mockAuthHeader) } throws Exception()

        settingsRemoteSource.getFavoritesList(mockAuthHeader).collect { result ->
            assertEquals(errorResponse.code(), result.code())
        }
        coVerify(exactly = 1) { backupService.getFavoriteSymbolList(mockAuthHeader) }
    }

    @Test
    fun `should return internet error when exception happens for weight table`() = runBlocking {
        val errorResponse = Response.error<BackupWeightTableRequest>(600, internetErrorResponseBody)
        coEvery { backupService.getWeightTable(mockAuthHeader) } throws Exception()

        settingsRemoteSource.getWeightTable(mockAuthHeader).collect { result ->
            assertEquals(errorResponse.code(), result.code())
        }
        coVerify(exactly = 1) { backupService.getWeightTable(mockAuthHeader) }
    }

}