package com.example.speechbuddy.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.speechbuddy.R
import com.example.speechbuddy.data.remote.requests.AuthLoginRequest
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.repository.SettingsRepository
import com.example.speechbuddy.repository.UserRepository
import com.example.speechbuddy.ui.models.LoginErrorType
import com.example.speechbuddy.utils.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @MockK
    private val mockAuthRepository: AuthRepository = mockk()
    private val mockUserRepository: UserRepository = mockk()
    private val mockSettingsRepository: SettingsRepository = mockk()
    private val mockSessionManager: SessionManager = mockk()
    private lateinit var viewModel: LoginViewModel

    // boundary condition: 8 characters in password field
    private val validPassword = "password"
    private val shortPassword = "pwd"
    private val wrongPassword = "wrong_password" // valid format
    private val validEmail = "valid@test.com"
    private val invalidEmail = "invalid"
    private val unregisteredEmail = "unregistered@test.com" // valid format

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = LoginViewModel(
            mockAuthRepository,
            mockUserRepository,
            mockSettingsRepository,
            mockSessionManager
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `should set invalid email before login click when set email is called with invalid email`() {
        viewModel.setEmail(invalidEmail)

        assertEquals(invalidEmail, viewModel.emailInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
    }

    @Test
    fun `should set valid email before login click when set email is called with valid email`() {
        viewModel.setEmail(validEmail)

        assertEquals(validEmail, viewModel.emailInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
    }

    @Test
    fun `should set error type email after login click when set email is called with invalid email`() {
        viewModel.setEmail(invalidEmail)

        viewModel.login()

        assertEquals(invalidEmail, viewModel.emailInput)
        assertEquals(LoginErrorType.EMAIL, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
    }

    @Test
    fun `should set error type null after login click when invalid email is changed to valid email`() {
        viewModel.setEmail(invalidEmail)

        viewModel.login()

        viewModel.setEmail(validEmail)

        assertEquals(validEmail, viewModel.emailInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(true, viewModel.uiState.value.isValidEmail)
    }

    @Test
    fun `should set error type email after login click when set email is called with unregistered email`() {
        viewModel.setEmail(unregisteredEmail)
        viewModel.setPassword(validPassword)

        coEvery {
            mockAuthRepository.login(AuthLoginRequest(unregisteredEmail, validPassword))
        } returns flowOf(
            Resource.error(
                "{\"code\":400,\"message\":{\"wrong_email\":[\"wrong email address\"]}}",
                null
            )
        )

        viewModel.login()
        Thread.sleep(10) //viewModel.login() does not immediately produce result

        assertEquals(unregisteredEmail, viewModel.emailInput)
        assertEquals(LoginErrorType.EMAIL, viewModel.uiState.value.error?.type)
        assertEquals(R.string.wrong_email, viewModel.uiState.value.error?.messageId)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
    }

    @Test
    fun `should set error type null after login click when unregistered email is changed to valid email`() {
        viewModel.setEmail(unregisteredEmail)
        viewModel.setPassword(validPassword)

        coEvery {
            mockAuthRepository.login(AuthLoginRequest(unregisteredEmail, validPassword))
        } returns flowOf(
            Resource.error(
                "{\"code\":400,\"message\":{\"wrong_email\":[\"wrong email address\"]}}",
                null
            )
        )

        viewModel.login()
        Thread.sleep(10) //viewModel.login() does not immediately produce result

        viewModel.setEmail(validEmail)

        assertEquals(validEmail, viewModel.emailInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(null, viewModel.uiState.value.error?.messageId)
        assertEquals(true, viewModel.uiState.value.isValidEmail)
    }

    @Test
    fun `should set error type not email after login click when set email is called with valid email`() {
        viewModel.setEmail(validEmail)
        viewModel.setPassword(wrongPassword)

        coEvery {
            mockAuthRepository.login(AuthLoginRequest(validEmail, wrongPassword))
        } returns flowOf(
            Resource.error(
                "{\"code\":400,\"message\":{\"wrong_password\":[\"wrong password\"]}}",
                null
            )
        )

        viewModel.login()
        Thread.sleep(10) //viewModel.login() does not immediately produce result

        assertEquals(validEmail, viewModel.emailInput)
        assertEquals(LoginErrorType.PASSWORD, viewModel.uiState.value.error?.type)
        assertEquals(R.string.wrong_password, viewModel.uiState.value.error?.messageId)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
    }

    @Test
    fun `should set error type not email after login click when valid email is changed to invalid email`() {
        viewModel.setEmail(validEmail)
        viewModel.setPassword(wrongPassword)

        coEvery {
            mockAuthRepository.login(AuthLoginRequest(validEmail, wrongPassword))
        } returns flowOf(
            Resource.error(
                "{\"code\":400,\"message\":{\"wrong_password\":[\"wrong password\"]}}",
                null
            )
        )

        viewModel.login()
        Thread.sleep(10) //viewModel.login() does not immediately produce result

        viewModel.setEmail(invalidEmail)

        assertEquals(invalidEmail, viewModel.emailInput)
        assertEquals(LoginErrorType.PASSWORD, viewModel.uiState.value.error?.type)
        assertEquals(R.string.wrong_password, viewModel.uiState.value.error?.messageId)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
    }

    @Test
    fun `should set short password before login click when set password is called with short email`() {
        viewModel.setPassword(shortPassword)

        assertEquals(shortPassword, viewModel.passwordInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
    }

    @Test
    fun `should set valid password before login click when set password is called with valid email`() {
        viewModel.setPassword(validPassword)

        assertEquals(validPassword, viewModel.passwordInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
    }

    @Test
    fun `should set error type password after login click when set password is called with short password`() {
        viewModel.setEmail(validEmail)
        viewModel.setPassword(shortPassword)

        viewModel.login()

        assertEquals(shortPassword, viewModel.passwordInput)
        assertEquals(LoginErrorType.PASSWORD, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
    }

    @Test
    fun `should set error type null after login click when short password is changed to valid password`() {
        viewModel.setEmail(validEmail)
        viewModel.setPassword(shortPassword)

        viewModel.login()

        viewModel.setPassword(validPassword)

        assertEquals(validPassword, viewModel.passwordInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(true, viewModel.uiState.value.isValidPassword)
    }

    @Test
    fun `should set error type password after login click when set password is called with wrong password`() {
        viewModel.setEmail(validEmail)
        viewModel.setPassword(wrongPassword)

        coEvery {
            mockAuthRepository.login(AuthLoginRequest(validEmail, wrongPassword))
        } returns flowOf(
            Resource.error(
                "{\"code\":400,\"message\":{\"wrong_password\":[\"wrong password\"]}}",
                null
            )
        )

        viewModel.login()
        Thread.sleep(10) //viewModel.login() does not immediately produce result

        assertEquals(wrongPassword, viewModel.passwordInput)
        assertEquals(LoginErrorType.PASSWORD, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
    }

    @Test
    fun `should set error type null after login click when wrong password is changed to valid password`() {
        viewModel.setEmail(validEmail)
        viewModel.setPassword(wrongPassword)

        coEvery {
            mockAuthRepository.login(AuthLoginRequest(validEmail, wrongPassword))
        } returns flowOf(
            Resource.error(
                "{\"code\":400,\"message\":{\"wrong_password\":[\"wrong password\"]}}",
                null
            )
        )

        viewModel.login()
        Thread.sleep(10) //viewModel.login() does not immediately produce result

        viewModel.setPassword(validPassword)

        assertEquals(validPassword, viewModel.passwordInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(true, viewModel.uiState.value.isValidPassword)
    }

    @Test
    fun `should set error type email when login is called with invalid email, short password`() {
        viewModel.setEmail(invalidEmail)
        viewModel.setPassword(shortPassword)

        viewModel.login()

        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(LoginErrorType.EMAIL, viewModel.uiState.value.error?.type)
        assertEquals(R.string.wrong_email, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should set error type email when login is called with invalid email, valid password`() {
        viewModel.setEmail(invalidEmail)
        viewModel.setPassword(validPassword)

        viewModel.login()

        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(LoginErrorType.EMAIL, viewModel.uiState.value.error?.type)
        assertEquals(R.string.wrong_email, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should set error type email when login is called with unregistered email, short password`() {
        viewModel.setEmail(unregisteredEmail)
        viewModel.setPassword(shortPassword)

        viewModel.login()

        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(LoginErrorType.PASSWORD, viewModel.uiState.value.error?.type)
        assertEquals(R.string.wrong_password, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should set error type email when login is called with unregistered email, valid password`() {
        viewModel.setEmail(unregisteredEmail)
        viewModel.setPassword(validPassword)

        coEvery {
            mockAuthRepository.login(AuthLoginRequest(unregisteredEmail, validPassword))
        } returns flowOf(
            Resource.error(
                "{\"code\":400,\"message\":{\"wrong_email\":[\"wrong email address\"]}}",
                null
            )
        )

        viewModel.login()
        Thread.sleep(10) //viewModel.login() does not immediately produce result

        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(LoginErrorType.EMAIL, viewModel.uiState.value.error?.type)
        assertEquals(R.string.wrong_email, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should set error type password when login is called with valid email, short password`() {
        viewModel.setEmail(validEmail)
        viewModel.setPassword(shortPassword)

        viewModel.login()

        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(LoginErrorType.PASSWORD, viewModel.uiState.value.error?.type)
        assertEquals(R.string.wrong_password, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should set error type password when login is called with valid email, wrong password`() {
        viewModel.setEmail(validEmail)
        viewModel.setPassword(wrongPassword)

        coEvery {
            mockAuthRepository.login(AuthLoginRequest(validEmail, wrongPassword))
        } returns flowOf(
            Resource.error(
                "{\"code\":400,\"message\":{\"wrong_password\":[\"wrong password\"]}}",
                null
            )
        )

        viewModel.login()
        Thread.sleep(10) //viewModel.login() does not immediately produce result

        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(LoginErrorType.PASSWORD, viewModel.uiState.value.error?.type)
        assertEquals(R.string.wrong_password, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should login success when login is called with valid email, valid password`() {
        viewModel.setEmail(validEmail)
        viewModel.setPassword(validPassword)

        val authToken = AuthToken("access", "refresh")

        coEvery {
            mockAuthRepository.login(AuthLoginRequest(validEmail, validPassword))
        } returns flowOf(
            Resource.success(authToken)
        )
        coEvery { mockSessionManager.setAuthToken(authToken) } returns Unit
        coEvery { mockSessionManager.cachedToken.value } returns AuthToken("access", "refresh")

        viewModel.login()
        Thread.sleep(10) //viewModel.login() does not immediately produce result

        assertEquals(authToken, mockSessionManager.cachedToken.value)
    }

    @Test
    fun `should check previous user when called`() = runBlocking {
        val authToken = AuthToken("access", "refresh")

        coEvery { mockAuthRepository.checkPreviousUser() } returns flowOf(Resource.success(Pair(1, authToken)))
        coEvery { mockSessionManager.setUserId(1) } returns Unit
        coEvery { mockSessionManager.setAuthToken(authToken) } returns Unit

        viewModel.checkPreviousUser()

        coVerify { mockAuthRepository.checkPreviousUser() }
        coVerify { mockSessionManager.setUserId(1) }
        coVerify { mockSessionManager.setAuthToken(authToken) }
    }

    @Test
    fun `should enter guest mode when called`() = runBlocking {
        coEvery { mockUserRepository.setGuestMode() } returns Unit
        coEvery { mockSessionManager.enterGuestMode() } returns Unit

        viewModel.enterGuestMode()

        coVerify { mockUserRepository.setGuestMode() }
        coVerify { mockSessionManager.enterGuestMode() }
    }
}