package com.example.speechbuddy.data.remote

import com.example.speechbuddy.data.remote.models.MySymbolDto
import com.example.speechbuddy.service.SymbolCreationService
import com.example.speechbuddy.utils.ResponseHandler
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class MySymbolRemoteSourceTest {
    private lateinit var symbolCreationService: SymbolCreationService
    private lateinit var mySymbolRemoteSource: MySymbolRemoteSource
    private val responseHandler = ResponseHandler()

    private val mockAccessToken = "testAccessToken"
    private val mockAuthHeader = "Bearer $mockAccessToken"

    private val internetErrorResponseBody =
        "{\"code\": 600, \"message\": \"No Internet Connection\"}".toResponseBody()

    private val errorResponseBody =
        "{\"error\":\"Something went wrong\"}".toResponseBody("application/json".toMediaType())

    @Before
    fun setUp() {
        symbolCreationService = mockk()
        mySymbolRemoteSource = MySymbolRemoteSource(symbolCreationService, responseHandler)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should return MySymbolDto when request is valid for createSymbolBackup`() = runBlocking {
        val symbolText = "Test Symbol"
        val categoryId = 1
        val image = mockk<MultipartBody.Part>()

        val mySymbolDto = MySymbolDto(
            id = 1,
            imageUrl = "sampleUrl"
        )

        val expectedResponse = Response.success<MySymbolDto>(200, mySymbolDto)
        coEvery {
            symbolCreationService.createSymbolBackup(mockAuthHeader, any(), any(), image)
        } returns expectedResponse

        mySymbolRemoteSource.createSymbolBackup(mockAuthHeader, symbolText, categoryId, image).collect { result ->
            assertEquals(expectedResponse, result)
        }
        coVerify(exactly = 1) {
            symbolCreationService.createSymbolBackup(mockAuthHeader, any(), any(), image)
        }
    }

    @Test
    fun `should return error when request is invalid`() = runBlocking {
        val symbolText = "Test Symbol"
        val categoryId = 1
        val image = mockk<MultipartBody.Part>()
        val expectedResponse = Response.error<MySymbolDto>(400, errorResponseBody)
        coEvery {
            symbolCreationService.createSymbolBackup(mockAuthHeader, any(), any(), image)
        } returns expectedResponse

        mySymbolRemoteSource.createSymbolBackup(mockAuthHeader, symbolText, categoryId, image).collect { result ->
            assertEquals(expectedResponse, result)
        }
        coVerify(exactly = 1) {
            symbolCreationService.createSymbolBackup(mockAuthHeader, any(), any(), image)
        }
    }

    @Test
    fun `should return internet error when exception happens`() = runBlocking {
        val symbolText = "Test Symbol"
        val categoryId = 1
        val image = mockk<MultipartBody.Part>()
        val errorResponse = Response.error<MySymbolDto>(600, internetErrorResponseBody)
        coEvery {
            symbolCreationService.createSymbolBackup(mockAuthHeader, any(), any(), image)
        } throws Exception()

        mySymbolRemoteSource.createSymbolBackup(mockAuthHeader, symbolText, categoryId, image).collect { result ->
            assertEquals(errorResponse.code(), result.code())
        }
        coVerify(exactly = 1) {
            symbolCreationService.createSymbolBackup(mockAuthHeader, any(), any(), image)
        }
    }

}