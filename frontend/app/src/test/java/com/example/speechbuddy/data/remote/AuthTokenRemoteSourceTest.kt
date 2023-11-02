package com.example.speechbuddy.data.remote

import com.example.speechbuddy.data.remote.models.AuthTokenDto
import com.example.speechbuddy.data.remote.requests.AuthLoginRequest
import com.example.speechbuddy.data.remote.requests.AuthResetPasswordRequest
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailAcceptRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailSendRequest
import com.example.speechbuddy.service.AuthService
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.mockito.Mockito
import retrofit2.Response

class AuthTokenRemoteSourceTest {

    private lateinit var authTokenRemoteSource: AuthTokenRemoteSource
    private val authService: AuthService = Mockito.mock(AuthService::class.java)

    @Before
    fun setUp() {
        authTokenRemoteSource = AuthTokenRemoteSource(authService)
    }

    @Test
    fun `signupAuthToken emits correct response`(): Unit = runBlocking {
        // Arrange
        val request = AuthSignupRequest(email = "test@example.com", password = "password123", nickname = "testUser")
        val expectedResponse: Response<Void> = Mockito.mock(Response::class.java) as Response<Void>
        Mockito.`when`(authService.signup(request)).thenReturn(expectedResponse)

        // Act
        val result = authTokenRemoteSource.signupAuthToken(request).toList()

        // Assert
        assertEquals(listOf(expectedResponse), result)
        Mockito.verify(authService).signup(request)
    }

    @Test
    fun `loginAuthToken emits correct response`(): Unit = runBlocking {
        // Arrange
        val request = AuthLoginRequest(email = "test@example.com", password = "password123")
        val expectedResponse: Response<AuthTokenDto> = Mockito.mock(Response::class.java) as Response<AuthTokenDto>
        Mockito.`when`(authService.login(request)).thenReturn(expectedResponse)

        // Act
        val result = authTokenRemoteSource.loginAuthToken(request).toList()

        // Assert
        assertEquals(listOf(expectedResponse), result)
        Mockito.verify(authService).login(request)
    }

    @Test
    fun `verifySendSignupAuthToken emits correct response`(): Unit = runBlocking {
        val request = AuthVerifyEmailSendRequest(email = "test@example.com")
        val expectedResponse: Response<Void> = Mockito.mock(Response::class.java) as Response<Void>
        Mockito.`when`(authService.verifySendSignup(request)).thenReturn(expectedResponse)

        val result = authTokenRemoteSource.verifySendSignupAuthToken(request).toList()

        assertEquals(listOf(expectedResponse), result)
        Mockito.verify(authService).verifySendSignup(request)
    }

    @Test
    fun `verifySendPWAuthToken emits correct response`(): Unit = runBlocking {
        val request = AuthVerifyEmailSendRequest(email = "test@example.com")
        val expectedResponse: Response<Void> = Mockito.mock(Response::class.java) as Response<Void>
        Mockito.`when`(authService.verifySendPW(request)).thenReturn(expectedResponse)

        val result = authTokenRemoteSource.verifySendPWAuthToken(request).toList()

        assertEquals(listOf(expectedResponse), result)
        Mockito.verify(authService).verifySendPW(request)
    }

    @Test
    fun `verifyAcceptSignupAuthToken emits correct response`(): Unit = runBlocking {
        val request = AuthVerifyEmailAcceptRequest(email = "test@example.com", code = "123456")
        val expectedResponse: Response<Void> = Mockito.mock(Response::class.java) as Response<Void>
        Mockito.`when`(authService.verifyAcceptSignup(request)).thenReturn(expectedResponse)

        val result = authTokenRemoteSource.verifyAcceptSignupAuthToken(request).toList()

        assertEquals(listOf(expectedResponse), result)
        Mockito.verify(authService).verifyAcceptSignup(request)
    }

    @Test
    fun `verifyAcceptPWAuthToken emits correct response`(): Unit = runBlocking {
        val request = AuthVerifyEmailAcceptRequest(email = "test@example.com", code = "123456")
        val expectedResponse: Response<AuthTokenDto> = Mockito.mock(Response::class.java) as Response<AuthTokenDto>
        Mockito.`when`(authService.verifyAcceptPW(request)).thenReturn(expectedResponse)

        val result = authTokenRemoteSource.verifyAcceptPWAuthToken(request).toList()

        assertEquals(listOf(expectedResponse), result)
        Mockito.verify(authService).verifyAcceptPW(request)
    }

    @Test
    fun `resetPasswordAuthToken emits correct response`(): Unit = runBlocking {
        val request = AuthResetPasswordRequest(password = "newPassword123")
        val expectedResponse: Response<Void> = Mockito.mock(Response::class.java) as Response<Void>
        Mockito.`when`(authService.resetPassword(request)).thenReturn(expectedResponse)

        val result = authTokenRemoteSource.resetPasswordAuthToken(request).toList()

        assertEquals(listOf(expectedResponse), result)
        Mockito.verify(authService).resetPassword(request)
    }



    // Exception Test
    @Test(expected = Exception::class)
    fun `signupAuthToken throws Exception on failure`(): Unit = runBlocking {
        val request = AuthSignupRequest(email = "test@example.com", password = "password123", nickname = "testUser")
        val exception = RuntimeException("Signup failed")
        Mockito.`when`(authService.signup(request)).thenThrow(exception)

        authTokenRemoteSource.signupAuthToken(request).toList()
    }

    @Test(expected = Exception::class)
    fun `loginAuthToken throws Exception on failure`(): Unit = runBlocking {
        val request = AuthLoginRequest(email = "test@example.com", password = "password123")
        val exception = RuntimeException("Login failed")
        Mockito.`when`(authService.login(request)).thenThrow(exception)

        authTokenRemoteSource.loginAuthToken(request).toList()
    }

    @Test(expected = Exception::class)
    fun `verifySendSignupAuthToken throws Exception on failure`(): Unit = runBlocking {
        val request = AuthVerifyEmailSendRequest(email = "test@example.com")
        val exception = RuntimeException("Email verification send failed")
        Mockito.`when`(authService.verifySendSignup(request)).thenThrow(exception)

        authTokenRemoteSource.verifySendSignupAuthToken(request).toList()
    }

    @Test(expected = Exception::class)
    fun `verifySendPWAuthToken throws Exception on failure`(): Unit = runBlocking {
        val request = AuthVerifyEmailSendRequest(email = "test@example.com")
        val exception = RuntimeException("Password verification send failed")
        Mockito.`when`(authService.verifySendPW(request)).thenThrow(exception)

        authTokenRemoteSource.verifySendPWAuthToken(request).toList()
    }

    @Test(expected = Exception::class)
    fun `verifyAcceptSignupAuthToken throws Exception on failure`(): Unit = runBlocking {
        val request = AuthVerifyEmailAcceptRequest(email = "test@example.com", code = "123456")
        val exception = RuntimeException("Email verification accept failed")
        Mockito.`when`(authService.verifyAcceptSignup(request)).thenThrow(exception)

        authTokenRemoteSource.verifyAcceptSignupAuthToken(request).toList()
    }

    @Test(expected = Exception::class)
    fun `verifyAcceptPWAuthToken throws Exception on failure`(): Unit = runBlocking {
        val request = AuthVerifyEmailAcceptRequest(email = "test@example.com", code = "123456")
        val exception = RuntimeException("Password verification accept failed")
        Mockito.`when`(authService.verifyAcceptPW(request)).thenThrow(exception)

        authTokenRemoteSource.verifyAcceptPWAuthToken(request).toList()
    }

    @Test(expected = Exception::class)
    fun `resetPasswordAuthToken throws Exception on failure`(): Unit = runBlocking {
        val request = AuthResetPasswordRequest(password = "newPassword123")
        val exception = RuntimeException("Password reset failed")
        Mockito.`when`(authService.resetPassword(request)).thenThrow(exception)

        authTokenRemoteSource.resetPasswordAuthToken(request).toList()
    }
}