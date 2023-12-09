package com.example.speechbuddy.data.remote

import com.example.speechbuddy.data.remote.models.UserDto
import com.example.speechbuddy.service.UserService
import com.example.speechbuddy.utils.ResponseHandler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class UserRemoteSourceTest {

    private lateinit var userService: UserService
    private lateinit var userRemoteSource: UserRemoteSource
    private val responseHandler = ResponseHandler()

    private val mockAccessToken = "testAccessToken"
    private val mockAuthHeader = "Bearer $mockAccessToken"

    private val internetErrorResponseBody =
        "{\"code\": 600, \"message\": \"No Internet Connection\"}".toResponseBody()

    private val errorResponseBody =
        "{\"error\":\"Something went wrong\"}".toResponseBody()

    @Before
    fun setUp() {
        userService = mockk()
        userRemoteSource = UserRemoteSource(userService, responseHandler)
    }

    @Test
    fun `should return UserDto when request is valid`() = runBlocking {
        val expectedResponse: Response<UserDto> = mockk(relaxed = true)
        coEvery { userService.getMyInfo(mockAuthHeader) } returns expectedResponse

        userRemoteSource.getMyInfoUser(mockAuthHeader).collect { result ->
            assertEquals(expectedResponse, result)
        }
        coVerify(exactly = 1) { userService.getMyInfo(mockAuthHeader) }
    }

    @Test
    fun `should return error when request is invalid`() = runBlocking {
        val expectedResponse = Response.error<UserDto>(400, errorResponseBody)
        coEvery { userService.getMyInfo(mockAuthHeader) } returns expectedResponse

        userRemoteSource.getMyInfoUser(mockAuthHeader).collect { result ->
            assertEquals(expectedResponse, result)
        }
        coVerify(exactly = 1) { userService.getMyInfo(mockAuthHeader) }
    }

    @Test
    fun `should return internet error when exception happens`() = runBlocking {
        val errorResponse = Response.error<UserDto>(600, internetErrorResponseBody)
        coEvery { userService.getMyInfo(mockAuthHeader) } throws Exception()

        userRemoteSource.getMyInfoUser(mockAuthHeader).collect { result ->
            assertEquals(errorResponse.code(), result.code())
        }
        coVerify(exactly = 1) { userService.getMyInfo(mockAuthHeader) }
    }

}