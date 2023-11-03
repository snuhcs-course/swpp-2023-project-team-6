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
    fun signup_AuthSignupRequest_returnResponseSuccess() = runBlocking {
        val signupRequest = AuthSignupRequest(mockEmail, mockPassword, mockNickname)
        coEvery { authService.signup(signupRequest) } returns Response.success(null)

        val response = authService.signup(signupRequest)

        coVerify(exactly = 1) { authService.signup(signupRequest) }
        assertTrue(response.isSuccessful)
    }

    @Test
    fun login_AuthLoginRequest_returnResponseSuccess() = runBlocking {
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
    fun verifySendSignup_AuthVerifyEmailSendRequest_returnResponseSuccess() = runBlocking {
        val verifyEmailSendRequest = AuthVerifyEmailSendRequest(mockEmail)
        coEvery { authService.verifySendSignup(verifyEmailSendRequest) } returns Response.success(
            null
        )

        val response = authService.verifySendSignup(verifyEmailSendRequest)

        coVerify(exactly = 1) { authService.verifySendSignup(verifyEmailSendRequest) }
        assertTrue(response.isSuccessful)
    }

    @Test
    fun verifySendPW_AuthVerifyEmailSendRequest_returnResponseSuccess() = runBlocking {
        val verifyEmailSendRequest = AuthVerifyEmailSendRequest(mockEmail)
        coEvery { authService.verifySendPW(verifyEmailSendRequest) } returns Response.success(null)

        val response = authService.verifySendPW(verifyEmailSendRequest)

        coVerify(exactly = 1) { authService.verifySendPW(verifyEmailSendRequest) }
        assertTrue(response.isSuccessful)
    }

    @Test
    fun verifyAcceptSignup_AuthVerifyEmailAcceptRequest_returnResponseSuccess() = runBlocking {
        val verifyEmailAcceptRequest = AuthVerifyEmailAcceptRequest(mockEmail, mockCode)
        coEvery { authService.verifyAcceptSignup(verifyEmailAcceptRequest) } returns Response.success(
            null
        )

        val response = authService.verifyAcceptSignup(verifyEmailAcceptRequest)

        coVerify(exactly = 1) { authService.verifyAcceptSignup(verifyEmailAcceptRequest) }
        assertTrue(response.isSuccessful)
    }

    @Test
    fun verifyAcceptPW_AuthVerifyEmailAccpetRequest_returnResponseSuccess() = runBlocking {
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
    fun resetPassword_AuthResetPasswordRequest_returnResponseSuccess() = runBlocking {
        val resetPasswordRequest = AuthResetPasswordRequest(mockPassword)
        coEvery { authService.resetPassword(resetPasswordRequest) } returns Response.success(null)

        val response = authService.resetPassword(resetPasswordRequest)

        coVerify(exactly = 1) { authService.resetPassword(resetPasswordRequest) }
        assertTrue(response.isSuccessful)
    }


    // Test Error Case
    @Test
    fun signup_AuthSignupRequest_returnResponseFail() = runBlocking {
        val signupRequest = AuthSignupRequest(mockEmail, "short", mockNickname)
        coEvery { authService.signup(signupRequest) } returns Response.error(400, errorResponseBody)

        val response = authService.signup(signupRequest)

        coVerify(exactly = 1) { authService.signup(signupRequest) }
        assertFalse(response.isSuccessful)
    }

    @Test
    fun login_AuthLoginRequest_returnResponseFail() = runBlocking {
        val loginRequest = AuthLoginRequest(mockEmail, "wrongpassword")
        coEvery { authService.login(loginRequest) } returns Response.error(401, errorResponseBody)

        val response = authService.login(loginRequest)

        coVerify(exactly = 1) { authService.login(loginRequest) }
        assertFalse(response.isSuccessful)
    }

    @Test
    fun verifySendSignup_AuthVerifyEmailSendRequest_returnResponseFail() = runBlocking {
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
    fun verifySendPW_AuthVerifyEmailSendRequest_returnResponseFail() = runBlocking {
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
    fun verifyAcceptSignup_AuthVerifyEmailAcceptRequest_returnResponseFail() = runBlocking {
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
    fun verifyAcceptPW_AuthVerifyEmailAcceptRequest_returnResponseFail() = runBlocking {
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
    fun resetPassword_AuthResetPasswordRequest_returnResponseFail() = runBlocking {
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
