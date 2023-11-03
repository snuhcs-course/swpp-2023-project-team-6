package com.example.speechbuddy.service

import com.example.speechbuddy.data.remote.models.AuthTokenDto
import com.example.speechbuddy.data.remote.requests.AuthLoginRequest
import com.example.speechbuddy.data.remote.requests.AuthResetPasswordRequest
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailAcceptRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailSendRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class AuthServiceTest {
    private lateinit var authService: AuthService
    private val mockEmail = "test@example.com"
    private val mockPassword = "password123"
    private val mockNickname = "TestUser"
    private val mockCode = "123456"
    private val errorResponseBody =
        "{\"error\":\"Something went wrong\"}".toResponseBody("application/json".toMediaType())

    @Before
    fun setUp() {
        authService = mockk()
    }

    @Test
    fun `should return response success when signup request is valid`() = runBlocking {
        val signupRequest = AuthSignupRequest(mockEmail, mockPassword, mockNickname)
        coEvery { authService.signup(signupRequest) } returns Response.success(null)

        val response = authService.signup(signupRequest)

        coVerify(exactly = 1) { authService.signup(signupRequest) }
        assertTrue(response.isSuccessful)
    }

    @Test
    fun `should return response with auth token dto when login request is valid`() = runBlocking {
        val loginRequest = AuthLoginRequest(mockEmail, mockPassword)
        val authTokenDto = AuthTokenDto("AccessToken", "RefreshToken")
        coEvery { authService.login(loginRequest) } returns Response.success(authTokenDto)

        val response = authService.login(loginRequest)

        coVerify(exactly = 1) { authService.login(loginRequest) }
        assertTrue(response.isSuccessful)
        assertTrue(response.body()?.accessToken == "AccessToken")
        assertTrue(response.body()?.refreshToken == "RefreshToken")
    }

    @Test
    fun `should return response success when request email send is valid for signup`() = runBlocking {
        val verifyEmailSendRequest = AuthVerifyEmailSendRequest(mockEmail)
        coEvery { authService.verifySendSignup(verifyEmailSendRequest) } returns Response.success(
            null
        )

        val response = authService.verifySendSignup(verifyEmailSendRequest)

        coVerify(exactly = 1) { authService.verifySendSignup(verifyEmailSendRequest) }
        assertTrue(response.isSuccessful)
    }

    @Test
    fun `should return response success when request email send is valid for password reset`() = runBlocking {
        val verifyEmailSendRequest = AuthVerifyEmailSendRequest(mockEmail)
        coEvery { authService.verifySendPW(verifyEmailSendRequest) } returns Response.success(null)

        val response = authService.verifySendPW(verifyEmailSendRequest)

        coVerify(exactly = 1) { authService.verifySendPW(verifyEmailSendRequest) }
        assertTrue(response.isSuccessful)
    }

    @Test
    fun `should return response with success when request code is valid for signup`() = runBlocking {
        val verifyEmailAcceptRequest = AuthVerifyEmailAcceptRequest(mockEmail, mockCode)
        coEvery { authService.verifyAcceptSignup(verifyEmailAcceptRequest) } returns Response.success(
            null
        )

        val response = authService.verifyAcceptSignup(verifyEmailAcceptRequest)

        coVerify(exactly = 1) { authService.verifyAcceptSignup(verifyEmailAcceptRequest) }
        assertTrue(response.isSuccessful)
    }

    @Test
    fun `should return response with auth token dto when request code is valid for password reset`() = runBlocking {
        val verifyEmailAcceptRequest = AuthVerifyEmailAcceptRequest(mockEmail, mockCode)
        val authTokenDto = AuthTokenDto("AccessToken", "RefreshToken")
        coEvery { authService.verifyAcceptPW(verifyEmailAcceptRequest) } returns Response.success(
            authTokenDto
        )

        val response = authService.verifyAcceptPW(verifyEmailAcceptRequest)

        coVerify(exactly = 1) { authService.verifyAcceptPW(verifyEmailAcceptRequest) }
        assertTrue(response.isSuccessful)
        assertTrue(response.body()?.accessToken == "AccessToken")
        assertTrue(response.body()?.refreshToken == "RefreshToken")
    }

    @Test
    fun `should return response with success when request is valid password`() = runBlocking {
        val resetPasswordRequest = AuthResetPasswordRequest(mockPassword)
        coEvery { authService.resetPassword(resetPasswordRequest) } returns Response.success(null)

        val response = authService.resetPassword(resetPasswordRequest)

        coVerify(exactly = 1) { authService.resetPassword(resetPasswordRequest) }
        assertTrue(response.isSuccessful)
    }


    // Test Error Case
    @Test
    fun `should return response with error when signup request is invalid`() = runBlocking {
        val signupRequest = AuthSignupRequest(mockEmail, "short", mockNickname)
        coEvery { authService.signup(signupRequest) } returns Response.error(400, errorResponseBody)

        val response = authService.signup(signupRequest)

        coVerify(exactly = 1) { authService.signup(signupRequest) }
        assertFalse(response.isSuccessful)
    }

    @Test
    fun `should return response with error when login request is invalid`() = runBlocking {
        val loginRequest = AuthLoginRequest(mockEmail, "wrongpassword")
        coEvery { authService.login(loginRequest) } returns Response.error(401, errorResponseBody)

        val response = authService.login(loginRequest)

        coVerify(exactly = 1) { authService.login(loginRequest) }
        assertFalse(response.isSuccessful)
    }

    @Test
    fun `should return response with error when reques temail send is invalid for signup`() = runBlocking {
        val verifyEmailSendRequest = AuthVerifyEmailSendRequest("invalid_email")
        coEvery { authService.verifySendSignup(verifyEmailSendRequest) } returns Response.error(
            400,
            errorResponseBody
        )

        val response = authService.verifySendSignup(verifyEmailSendRequest)

        coVerify(exactly = 1) { authService.verifySendSignup(verifyEmailSendRequest) }
        assertFalse(response.isSuccessful)
    }

    @Test
    fun `should return response with error when request email send is invalid for password reset`() = runBlocking {
        val verifyEmailSendRequest = AuthVerifyEmailSendRequest("invalid_email")
        coEvery { authService.verifySendPW(verifyEmailSendRequest) } returns Response.error(
            400,
            errorResponseBody
        )

        val response = authService.verifySendPW(verifyEmailSendRequest)

        coVerify(exactly = 1) { authService.verifySendPW(verifyEmailSendRequest) }
        assertFalse(response.isSuccessful)
    }

    @Test
    fun `should return response with error when request code is invalid for signup`() = runBlocking {
        val verifyEmailAcceptRequest = AuthVerifyEmailAcceptRequest(mockEmail, "wrongcode")
        coEvery { authService.verifyAcceptSignup(verifyEmailAcceptRequest) } returns Response.error(
            400,
            errorResponseBody
        )

        val response = authService.verifyAcceptSignup(verifyEmailAcceptRequest)

        coVerify(exactly = 1) { authService.verifyAcceptSignup(verifyEmailAcceptRequest) }
        assertFalse(response.isSuccessful)
    }

    @Test
    fun `should return response with error when request code is invalid for password reset`() = runBlocking {
        val verifyEmailAcceptRequest = AuthVerifyEmailAcceptRequest(mockEmail, "wrongcode")
        coEvery { authService.verifyAcceptPW(verifyEmailAcceptRequest) } returns Response.error(
            400,
            errorResponseBody
        )

        val response = authService.verifyAcceptPW(verifyEmailAcceptRequest)

        coVerify(exactly = 1) { authService.verifyAcceptPW(verifyEmailAcceptRequest) }
        assertFalse(response.isSuccessful)
    }

    @Test
    fun `should return response with error when request is invalid password`() = runBlocking {
        val resetPasswordRequest = AuthResetPasswordRequest("short")
        coEvery { authService.resetPassword(resetPasswordRequest) } returns Response.error(
            400,
            errorResponseBody
        )

        val response = authService.resetPassword(resetPasswordRequest)

        coVerify(exactly = 1) { authService.resetPassword(resetPasswordRequest) }
        assertFalse(response.isSuccessful)
    }
}
