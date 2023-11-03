package com.example.speechbuddy.data.remote

import com.example.speechbuddy.data.remote.models.AuthTokenDto
import com.example.speechbuddy.data.remote.requests.AuthLoginRequest
import com.example.speechbuddy.data.remote.requests.AuthResetPasswordRequest
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailAcceptRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailSendRequest
import com.example.speechbuddy.service.AuthService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class AuthTokenRemoteSourceTest {

    private lateinit var authService: AuthService
    private lateinit var authTokenRemoteSource: AuthTokenRemoteSource

    @Before
    fun setUp() {
        authService = mockk()
        authTokenRemoteSource = AuthTokenRemoteSource(authService)
    }


    @Test
    fun signupAuthToken_AuthSignupRequest_returnFlowResponse() = runBlocking {
        // Arrange
        val request = AuthSignupRequest(
            email = "test@example.com",
            password = "password123",
            nickname = "testUser"
        )
        val expectedResponse: Response<Void> = mockk(relaxed = true)
        coEvery { authService.signup(request) } returns expectedResponse

        // Act
        val result = authTokenRemoteSource.signupAuthToken(request).first()

        // Assert
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { authService.signup(request) }
    }

    @Test
    fun loginAuthToken_AuthLoginRequest_returnFlowResponse() = runBlocking {
        val request = AuthLoginRequest(email = "test@example.com", password = "password123")
        val expectedResponse: Response<AuthTokenDto> = mockk(relaxed = true)

        coEvery { authService.login(request) } returns expectedResponse

        val result = authTokenRemoteSource.loginAuthToken(request).first()
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { authService.login(request) }
    }

    @Test
    fun verifySendSignupAuthToken_AuthVerifyEmailSendRequest_returnFlowResponse() = runBlocking {
        val request = AuthVerifyEmailSendRequest(email = "test@example.com")
        val expectedResponse: Response<Void> = mockk(relaxed = true)

        coEvery { authService.verifySendSignup(request) } returns expectedResponse

        val result = authTokenRemoteSource.verifySendSignupAuthToken(request).first()
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { authService.verifySendSignup(request) }
    }

    @Test
    fun verifySendPWAuthToken_AuthVerifyEmailSendRequest_returnFlowResponse() = runBlocking {
        val request = AuthVerifyEmailSendRequest(email = "test@example.com")
        val expectedResponse: Response<Void> = mockk(relaxed = true)

        coEvery { authService.verifySendPW(request) } returns expectedResponse

        val result = authTokenRemoteSource.verifySendPWAuthToken(request).first()
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { authService.verifySendPW(request) }
    }

    @Test
    fun verifyAcceptSignupAuthToken_AuthVerifyEmailAcceptRequest_returnFlowResponse() =
        runBlocking {
            val request = AuthVerifyEmailAcceptRequest(email = "test@example.com", code = "123456")
            val expectedResponse: Response<Void> = mockk(relaxed = true)

            coEvery { authService.verifyAcceptSignup(request) } returns expectedResponse

            val result = authTokenRemoteSource.verifyAcceptSignupAuthToken(request).first()
            assertEquals(expectedResponse, result)
            coVerify(exactly = 1) { authService.verifyAcceptSignup(request) }
        }

    @Test
    fun verifyAcceptPWAuthToken_AuthVerifyEmailAcceptRequest_returnFlowResponse() = runBlocking {
        val request = AuthVerifyEmailAcceptRequest(email = "test@example.com", code = "123456")
        val expectedResponse: Response<AuthTokenDto> = mockk(relaxed = true)

        coEvery { authService.verifyAcceptPW(request) } returns expectedResponse

        val result = authTokenRemoteSource.verifyAcceptPWAuthToken(request).first()
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { authService.verifyAcceptPW(request) }
    }

    @Test
    fun resetPasswordAuthToken_AuthResetPasswordRequest_returnFlowResponse() = runBlocking {
        val request = AuthResetPasswordRequest(password = "newPassword123")
        val expectedResponse: Response<Void> = mockk(relaxed = true)

        coEvery { authService.resetPassword(request) } returns expectedResponse

        val result = authTokenRemoteSource.resetPasswordAuthToken(request).first()
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { authService.resetPassword(request) }
    }


    // Test Exception
    @Test(expected = Exception::class)
    fun signupAuthToken_AuthSignupRequest_returnException(): Unit = runBlocking {
        val request = AuthSignupRequest(
            email = "test@example.com",
            password = "password123",
            nickname = "testUser"
        )
        val expectedResponse = RuntimeException("Signup failed")
        coEvery { authService.signup(request) } throws Exception(expectedResponse)

        val result = authTokenRemoteSource.signupAuthToken(request).toList()

        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { authTokenRemoteSource.signupAuthToken(request) }
    }

    @Test(expected = Exception::class)
    fun loginAuthToken_AuthLoginRequest_returnException(): Unit = runBlocking {
        val request = AuthLoginRequest(email = "test@example.com", password = "password123")
        val expectedResponse = RuntimeException("Login failed")
        coEvery { authService.login(request) } throws Exception(expectedResponse)

        val result = authTokenRemoteSource.loginAuthToken(request).toList()

        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { authTokenRemoteSource.loginAuthToken(request) }
    }

    @Test(expected = Exception::class)
    fun verifySendSignupAuthToken_AuthVerifyEmailSendRequest_returnException(): Unit = runBlocking {
        val request = AuthVerifyEmailSendRequest(email = "test@example.com")
        coEvery { authService.verifySendSignup(request) } throws Exception("Email verification send failed")
        val expectedResponse = RuntimeException("Email verification send failed")

        val result = authTokenRemoteSource.verifySendSignupAuthToken(request).toList()

        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { authTokenRemoteSource.verifySendSignupAuthToken(request) }
    }

    @Test(expected = Exception::class)
    fun verifySendPWAuthToken_AuthVerifyEmailSendRequest_returnException(): Unit = runBlocking {
        val request = AuthVerifyEmailSendRequest(email = "test@example.com")
        val expectedResponse = RuntimeException("Password verification send failed")
        coEvery { authService.verifySendPW(request) } throws Exception("Password verification send failed")

        val result = authTokenRemoteSource.verifySendPWAuthToken(request).toList()

        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { authTokenRemoteSource.verifySendPWAuthToken(request) }
    }

    @Test(expected = Exception::class)
    fun verifyAcceptSignupAuthTokenAuthVerifyEmailAcceptRequest_returnException(): Unit =
        runBlocking {
            val request = AuthVerifyEmailAcceptRequest(email = "test@example.com", code = "123456")
            val expectedResponse = RuntimeException("Email verification accept failed")
            coEvery { authService.verifyAcceptSignup(request) } throws Exception("Email verification accept failed")

            val result = authTokenRemoteSource.verifyAcceptSignupAuthToken(request).toList()

            assertEquals(expectedResponse, result)
            coVerify(exactly = 1) { authTokenRemoteSource.verifyAcceptSignupAuthToken(request) }
        }

    @Test(expected = Exception::class)
    fun verifyAcceptPWAuthToken_AuthVerifyEmailAcceptRequest_returnException(): Unit = runBlocking {
        val request = AuthVerifyEmailAcceptRequest(email = "test@example.com", code = "123456")
        val expectedResponse = RuntimeException("Password verification accept failed")
        coEvery { authService.verifyAcceptPW(request) } throws Exception("Password verification accept failed")

        val result = authTokenRemoteSource.verifyAcceptPWAuthToken(request).toList()

        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { authTokenRemoteSource.verifyAcceptPWAuthToken(request) }
    }

    @Test(expected = Exception::class)
    fun resetPasswordAuthToken_AuthResetPasswordRequest_returnException(): Unit = runBlocking {
        val request = AuthResetPasswordRequest(password = "newPassword123")
        val expectedResponse = RuntimeException("Password reset failed")
        coEvery { authService.resetPassword(request) } throws Exception("Password reset failed")

        val result = authTokenRemoteSource.resetPasswordAuthToken(request).toList()

        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { authTokenRemoteSource.resetPasswordAuthToken(request) }
    }
}
