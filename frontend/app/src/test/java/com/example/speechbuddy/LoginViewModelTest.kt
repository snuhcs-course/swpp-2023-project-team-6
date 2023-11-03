package com.example.speechbuddy

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.speechbuddy.data.remote.requests.AuthLoginRequest
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.LoginErrorType
import com.example.speechbuddy.ui.models.SignupErrorType
import com.example.speechbuddy.utils.Resource
import com.example.speechbuddy.viewmodel.LoginViewModel
import com.example.speechbuddy.viewmodel.SignupViewModel
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class LoginViewModelTest {
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @MockK
    private val repository: AuthRepository = mockk()
    private lateinit var viewModel: LoginViewModel

    // boundary condition: 8 characters in password field
    private val validPassword = "password"
    private val shortPassword = "pwd"
    private val wrongPassword = "wrong_password"
    private val validEmail = "valid@test.com"
    private val invalidEmail = "invalid"
    private val notRegisteredEmail = "not_registered@test.com"
    private val emptyString = ""

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = LoginViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun loginViewModel_setEmail_invalid_beforeSignupClick() {
        viewModel.setEmail(invalidEmail)

        assertEquals(invalidEmail, viewModel.emailInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
    }

    @Test
    fun loginViewModel_setEmail_valid_beforeSignupClick() {
        viewModel.setEmail(validEmail)

        assertEquals(validEmail, viewModel.emailInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
    }

    @Test
    fun loginViewModel_setEmail_invalid_afterSignupClick() {
        viewModel.setEmail(invalidEmail)

        viewModel.login()

        assertEquals(invalidEmail, viewModel.emailInput)
        assertEquals(viewModel.uiState.value.error?.type, LoginErrorType.EMAIL)
        assertEquals(viewModel.uiState.value.isValidEmail, false)

        viewModel.setEmail(validEmail)

        assertEquals(validEmail, viewModel.emailInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidEmail, true)
    }

    @Test
    fun loginViewModel_setEmail_notRegistered_afterSignupClick() {
        viewModel.setEmail(notRegisteredEmail)
        viewModel.setPassword(validPassword)

        coEvery {
            repository.login(AuthLoginRequest(notRegisteredEmail, validPassword))
        } returns flowOf(
            Resource.error(
            "{\"code\":400,\"message\":{\"wrong_email\":[\"wrong email address\"]}}",
            null)
        )
        viewModel.login()

        assertEquals(notRegisteredEmail, viewModel.emailInput)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.false_email)
        assertEquals(viewModel.uiState.value.error?.type, LoginErrorType.EMAIL)
        assertEquals(viewModel.uiState.value.isValidEmail, false)

        viewModel.setEmail(validEmail)

        assertEquals(validEmail, viewModel.emailInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.error?.messageId, null)
        assertEquals(viewModel.uiState.value.isValidEmail, true)
    }
/*
    @Test
    fun loginViewModel_setEmail_valid_afterSignupClick() {
        viewModel.setEmail(invalidEmail)

        viewModel.login()

        assertEquals(invalidEmail, viewModel.emailInput)
        assertEquals(viewModel.uiState.value.error?.type, LoginErrorType.EMAIL)
        assertEquals(viewModel.uiState.value.isValidEmail, false)

        viewModel.setEmail(validEmail)

        assertEquals(validEmail, viewModel.emailInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidEmail, true)
    }

    @Test
    fun loginViewModel_setPassword_short_beforeSignupClick() {
        viewModel.setPassword(shortPassword)

        assertEquals(shortPassword, viewModel.passwordInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidPassword, false)
    }

    @Test
    fun loginViewModel_setPassword_valid_beforeSignupClick() {
        viewModel.setPassword(validPassword)

        assertEquals(validPassword, viewModel.passwordInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidPassword, false)
    }

    @Test
    fun loginViewModel_setPassword_short_afterSignupClick() {
        viewModel.setPassword(shortPassword)
        viewModel.setNickname(validNickname)

        viewModel.signup(validEmail)

        assertEquals(shortPassword, viewModel.passwordInput)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.PASSWORD)
        assertEquals(viewModel.uiState.value.isValidPassword, false)

        viewModel.setPassword(validPassword)

        assertEquals(validPassword, viewModel.passwordInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidPassword, true)
    }

    @Test
    fun loginViewModel_setPassword_wrong_afterSignupClick() {
        viewModel.setPassword(validPassword)

        viewModel.signup(validEmail)

        assertEquals(validPassword, viewModel.passwordInput)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.NICKNAME)
        assertEquals(viewModel.uiState.value.isValidPassword, false)

        viewModel.setPassword(shortPassword)

        assertEquals(shortPassword, viewModel.passwordInput)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.NICKNAME)
        assertEquals(viewModel.uiState.value.isValidPassword, false)
    }


    @Test
    fun loginViewModel_setPassword_valid_afterSignupClick() {
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(shortPassword)

        assertEquals(shortPassword, viewModel.passwordCheckInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
    }

    @Test
    fun loginViewModel_login_invalidEmail_shortPassword() {
        viewModel.setPassword(shortPassword)
        viewModel.setPasswordCheck(validPassword)

        viewModel.signup(validEmail)

        assertEquals(viewModel.uiState.value.isValidNickname, false)
        assertEquals(viewModel.uiState.value.isValidPassword, false)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.NICKNAME)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.nickname_length_error)
    }

    @Test
    fun loginViewModel_login_invalidEmail_validPassword() {
        viewModel.setNickname(emptyNickname)
        viewModel.setPassword(shortPassword)
        viewModel.setPasswordCheck(shortPassword)

        viewModel.signup(validEmail)

        assertEquals(viewModel.uiState.value.isValidNickname, false)
        assertEquals(viewModel.uiState.value.isValidPassword, false)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.NICKNAME)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.nickname_length_error)
    }

    @Test
    fun loginViewModel_login_notRegisteredEmail_shortPassword() {
        viewModel.setNickname(emptyNickname)
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(shortPassword)

        viewModel.signup(validEmail)

        assertEquals(viewModel.uiState.value.isValidNickname, false)
        assertEquals(viewModel.uiState.value.isValidPassword, false)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.NICKNAME)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.nickname_length_error)
    }

    @Test
    fun loginViewModel_login_notRegisteredEmail_validPassword() {
        viewModel.setNickname(emptyNickname)
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(validPassword)

        viewModel.signup(validEmail)

        assertEquals(viewModel.uiState.value.isValidNickname, false)
        assertEquals(viewModel.uiState.value.isValidPassword, false)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.NICKNAME)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.nickname_length_error)
    }

    @Test
    fun loginViewModel_login_validEmail_shortPassword() {
        viewModel.setNickname(longNickname)
        viewModel.setPassword(shortPassword)
        viewModel.setPasswordCheck(validPassword)

        viewModel.signup(validEmail)

        assertEquals(viewModel.uiState.value.isValidNickname, false)
        assertEquals(viewModel.uiState.value.isValidPassword, false)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.NICKNAME)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.nickname_length_error)
    }

    @Test
    fun loginViewModel_login_validEmail_wrongPassword() {
        viewModel.setNickname(longNickname)
        viewModel.setPassword(shortPassword)
        viewModel.setPasswordCheck(shortPassword)

        viewModel.signup(validEmail)

        assertEquals(viewModel.uiState.value.isValidNickname, false)
        assertEquals(viewModel.uiState.value.isValidPassword, false)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.NICKNAME)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.nickname_length_error)
    }

    @Test
    fun loginViewModel_login_validEmail_validPassword() {
        val authSignupRequest = AuthSignupRequest(validEmail, validPassword, validNickname)
        coEvery { repository.signup(authSignupRequest) } returns flowOf(Resource.success(null))

        viewModel.setNickname(validNickname)
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(validPassword)

        viewModel.signup(validEmail)

        assertEquals(viewModel.signupResult.value?.message, null)
        assertEquals(viewModel.signupResult.value?.data, null)
    }

    @Test
    fun loginViewModel_clearInputs() {
        viewModel.setNickname(validNickname)
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(validPassword)

        assertEquals(validNickname, viewModel.nicknameInput)
        assertEquals(validPassword, viewModel.passwordInput)
        assertEquals(validPassword, viewModel.passwordCheckInput)

        viewModel.clearInputs()

        assertEquals(emptyString, viewModel.nicknameInput)
        assertEquals(emptyString, viewModel.passwordInput)
        assertEquals(emptyString, viewModel.passwordCheckInput)
    }

 */


}