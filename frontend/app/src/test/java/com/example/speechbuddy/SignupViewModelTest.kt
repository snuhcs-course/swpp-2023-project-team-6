package com.example.speechbuddy

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.SignupErrorType
import com.example.speechbuddy.utils.Resource
import com.example.speechbuddy.viewmodel.SignupViewModel
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.DelicateCoroutinesApi
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

class SignupViewModelTest {

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @MockK
    private val repository: AuthRepository = mockk()
    private lateinit var viewModel: SignupViewModel

    // boundary condition: 15 characters in nickname field
    private val validNickname = "valid_nickname_"
    private val longNickname = "invalid_nickname"
    private val emptyNickname = ""

    // boundary condition: 8 characters in password field
    private val validPassword = "password"
    private val shortPassword = "pwd"
    private val validEmail = "test@test.com"
    private val emptyString = ""

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = SignupViewModel(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `should set empty nickname before signup click when setnickname is called with empty nickname`() {
        viewModel.setNickname(emptyNickname)

        assertEquals(emptyNickname, viewModel.nicknameInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidNickname)
    }

    @Test
    fun `should set long nickname before signup click when setnickname is called with long nickname`() {
        viewModel.setNickname(longNickname)

        assertEquals(longNickname, viewModel.nicknameInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidNickname)
    }

    @Test
    fun `should set valid nickname before signup click when setnickname is called with valid nickname`() {
        viewModel.setNickname(validNickname)

        assertEquals(validNickname, viewModel.nicknameInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidNickname)
    }

    @Test
    fun `should set errortype nickname after signup click when setnickname is called with empty nickname`() {
        viewModel.setNickname(emptyNickname)

        viewModel.signup(validEmail)

        assertEquals(emptyNickname, viewModel.nicknameInput)
        assertEquals(SignupErrorType.NICKNAME, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidNickname)
    }

    @Test
    fun `should set errortype null after signup click when empty nickname is changed to valid nickname`() {
        viewModel.setNickname(emptyNickname)

        viewModel.signup(validEmail)

        viewModel.setNickname(validNickname)

        assertEquals(validNickname, viewModel.nicknameInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(true, viewModel.uiState.value.isValidNickname)
    }

    @Test
    fun `should set errortype nickname after signup click when setnickname is called with long nickname`() {
        viewModel.setNickname(longNickname)

        viewModel.signup(validEmail)

        assertEquals(longNickname, viewModel.nicknameInput)
        assertEquals(SignupErrorType.NICKNAME, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidNickname)
    }

    @Test
    fun `should set errortype null after signup click when long nickname is changed to valid nickname`() {
        viewModel.setNickname(longNickname)

        viewModel.signup(validEmail)

        viewModel.setNickname(validNickname)

        assertEquals(validNickname, viewModel.nicknameInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(true, viewModel.uiState.value.isValidNickname)
    }

    @Test
    fun `should set errortype not nickname after signup click when setnickname is called with valid nickname`() {
        viewModel.setNickname(validNickname)

        viewModel.signup("email@test.com")

        assertEquals(validNickname, viewModel.nicknameInput)
        assertEquals(SignupErrorType.PASSWORD, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidNickname)
    }

    @Test
    fun `should set errortype not nickname after signup click when valid nickname is changed to invalid nickname`() {
        viewModel.setNickname(validNickname)

        viewModel.signup("email@test.com")

        viewModel.setNickname(longNickname)

        assertEquals(longNickname, viewModel.nicknameInput)
        assertEquals(SignupErrorType.PASSWORD, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidNickname)
    }

    @Test
    fun `should set short password before signup click when setpassword is called with short password`() {
        viewModel.setPassword(shortPassword)

        assertEquals(shortPassword, viewModel.passwordInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
    }

    @Test
    fun `should set valid password before signup click when setpassword is called with valid password`() {
        viewModel.setPassword(validPassword)

        assertEquals(validPassword, viewModel.passwordInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
    }

    @Test
    fun `should set errortype password after signup click when setpassword is called with short password`() {
        viewModel.setPassword(shortPassword)
        viewModel.setNickname(validNickname)

        viewModel.signup(validEmail)

        assertEquals(shortPassword, viewModel.passwordInput)
        assertEquals(SignupErrorType.PASSWORD, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
    }

    @Test
    fun `should set errortype null after signup click when short password is changed to valid password`() {
        viewModel.setPassword(shortPassword)
        viewModel.setNickname(validNickname)

        viewModel.signup(validEmail)

        viewModel.setPassword(validPassword)

        assertEquals(validPassword, viewModel.passwordInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(true, viewModel.uiState.value.isValidPassword)
    }

    @Test
    fun `should set errortype not password after signup click when setpassword is called with valid password`() {
        viewModel.setPassword(validPassword)

        viewModel.signup(validEmail)

        assertEquals(validPassword, viewModel.passwordInput)
        assertEquals(SignupErrorType.NICKNAME, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
    }

    @Test
    fun `should set errortype not password after signup click when valid password is changed to invalid password`() {
        viewModel.setPassword(validPassword)

        viewModel.signup(validEmail)

        viewModel.setPassword(shortPassword)

        assertEquals(shortPassword, viewModel.passwordInput)
        assertEquals(SignupErrorType.NICKNAME, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
    }


    @Test
    fun `should set invalid passwordcheck before signup click when setpasswordcheck is called with invalid passwordcheck`() {
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(shortPassword)

        assertEquals(shortPassword, viewModel.passwordCheckInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
    }

    @Test
    fun `should set valid passwordcheck before signup click when setpasswordcheck is called with valid passwordcheck`() {
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(validPassword)

        assertEquals(validPassword, viewModel.passwordCheckInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
    }

    @Test
    fun `should set errortype email after signup click when setpasswordcheck is called with invalid passwordcheck`() {
        viewModel.setPassword(validPassword)
        viewModel.setNickname(validNickname)
        viewModel.setPasswordCheck(shortPassword)

        viewModel.signup(validEmail)

        assertEquals(shortPassword, viewModel.passwordCheckInput)
        assertEquals(SignupErrorType.PASSWORD_CHECK, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
    }

    @Test
    fun `should set errortype null after signup click when invalid passwordcheck is changed to valid passwordcheck`() {
        viewModel.setPassword(validPassword)
        viewModel.setNickname(validNickname)
        viewModel.setPasswordCheck(shortPassword)

        viewModel.signup(validEmail)

        viewModel.setPasswordCheck(validPassword)

        assertEquals(validPassword, viewModel.passwordCheckInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
        assertEquals(true, viewModel.uiState.value.isValidEmail)
    }

    @Test
    fun `should set errortype not email after signup click when setpasswordcheck is called with valid passwordcheck`() {
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(validPassword)

        viewModel.signup(validEmail)

        assertEquals(validPassword, viewModel.passwordCheckInput)
        assertEquals(SignupErrorType.NICKNAME, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
    }

    @Test
    fun `should set errortype not email after signup click when valid setpasswordcheck is changed to invalid passwordcheck`() {
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(validPassword)

        viewModel.signup(validEmail)

        viewModel.setPasswordCheck(shortPassword)

        assertEquals(shortPassword, viewModel.passwordCheckInput)
        assertEquals(SignupErrorType.NICKNAME, viewModel.uiState.value.error?.type)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
    }
    @Test
    fun `should set errortype nickname when signup is called with empty nickname, short password, invalid passwordcheck`() {
        viewModel.setNickname(emptyNickname)
        viewModel.setPassword(shortPassword)
        viewModel.setPasswordCheck(validPassword)

        viewModel.signup(validEmail)

        assertEquals(false, viewModel.uiState.value.isValidNickname)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(SignupErrorType.NICKNAME, viewModel.uiState.value.error?.type)
        assertEquals(R.string.nickname_length_error, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should set errortype nickname when signup is called with empty nickname, short password, valid passwordcheck`() {
        viewModel.setNickname(emptyNickname)
        viewModel.setPassword(shortPassword)
        viewModel.setPasswordCheck(shortPassword)

        viewModel.signup(validEmail)

        assertEquals(false, viewModel.uiState.value.isValidNickname)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(SignupErrorType.NICKNAME, viewModel.uiState.value.error?.type)
        assertEquals(R.string.nickname_length_error, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should set errortype nickname when signup is called with empty nickname, valid password, invalid passwordcheck`() {
        viewModel.setNickname(emptyNickname)
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(shortPassword)

        viewModel.signup(validEmail)

        assertEquals(false, viewModel.uiState.value.isValidNickname)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(SignupErrorType.NICKNAME, viewModel.uiState.value.error?.type)
        assertEquals(R.string.nickname_length_error, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should set errortype nickname when signup is called with empty nickname, valid password, valid passwordcheck`() {
        viewModel.setNickname(emptyNickname)
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(validPassword)

        viewModel.signup(validEmail)

        assertEquals(false, viewModel.uiState.value.isValidNickname)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(SignupErrorType.NICKNAME, viewModel.uiState.value.error?.type)
        assertEquals(R.string.nickname_length_error, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should set errortype nickname when signup is called with long nickname, short password, invalid passwordcheck`() {
        viewModel.setNickname(longNickname)
        viewModel.setPassword(shortPassword)
        viewModel.setPasswordCheck(validPassword)

        viewModel.signup(validEmail)

        assertEquals(false, viewModel.uiState.value.isValidNickname)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(SignupErrorType.NICKNAME, viewModel.uiState.value.error?.type)
        assertEquals(R.string.nickname_length_error, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should set errortype nickname when signup is called with long nickname, short password, valid passwordcheck`() {
        viewModel.setNickname(longNickname)
        viewModel.setPassword(shortPassword)
        viewModel.setPasswordCheck(shortPassword)

        viewModel.signup(validEmail)

        assertEquals(false, viewModel.uiState.value.isValidNickname)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(SignupErrorType.NICKNAME, viewModel.uiState.value.error?.type)
        assertEquals(R.string.nickname_length_error, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should set errortype nickname when signup is called with long nickname, valid password, invalid passwordcheck`() {
        viewModel.setNickname(longNickname)
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(shortPassword)

        viewModel.signup(validEmail)

        assertEquals(false, viewModel.uiState.value.isValidNickname)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(SignupErrorType.NICKNAME, viewModel.uiState.value.error?.type)
        assertEquals(R.string.nickname_length_error, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should set errortype nickname when signup is called with long nickname, valid password, valid passwordcheck`() {
        viewModel.setNickname(longNickname)
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(validPassword)

        viewModel.signup(validEmail)

        assertEquals(false, viewModel.uiState.value.isValidNickname)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(SignupErrorType.NICKNAME, viewModel.uiState.value.error?.type)
        assertEquals(R.string.nickname_length_error, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should set errortype password when signup is called with valid nickname, short password, invalid passwordcheck`() {
        viewModel.setNickname(validNickname)
        viewModel.setPassword(shortPassword)
        viewModel.setPasswordCheck(validPassword)

        viewModel.signup(validEmail)

        assertEquals(false, viewModel.uiState.value.isValidNickname)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(SignupErrorType.PASSWORD, viewModel.uiState.value.error?.type)
        assertEquals(R.string.false_new_password, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should set errortype password when signup is called with valid nickname, short password, valid passwordcheck`() {
        viewModel.setNickname(validNickname)
        viewModel.setPassword(shortPassword)
        viewModel.setPasswordCheck(shortPassword)

        viewModel.signup(validEmail)

        assertEquals(false, viewModel.uiState.value.isValidNickname)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(SignupErrorType.PASSWORD, viewModel.uiState.value.error?.type)
        assertEquals(R.string.false_new_password, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should set errortype passwordcheck when signup is called with valid nickname, valid password, invalid passwordcheck`() {
        viewModel.setNickname(validNickname)
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(shortPassword)

        viewModel.signup(validEmail)

        assertEquals(false, viewModel.uiState.value.isValidNickname)
        assertEquals(false, viewModel.uiState.value.isValidPassword)
        assertEquals(false, viewModel.uiState.value.isValidEmail)
        assertEquals(SignupErrorType.PASSWORD_CHECK, viewModel.uiState.value.error?.type)
        assertEquals(R.string.false_new_password_check, viewModel.uiState.value.error?.messageId)
    }

    @Test
    fun `should signup success when signup is called with valid nickname, valid password, valid passwordcheck`() = runTest {
        val authSignupRequest = AuthSignupRequest(validEmail, validPassword, validNickname)
        coEvery { repository.signup(authSignupRequest) } returns flowOf(Resource.success(null))

        viewModel.setNickname(validNickname)
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(validPassword)

        viewModel.signup(validEmail)

        assertEquals(null, viewModel.signupResult.value?.message)
        assertEquals(null, viewModel.signupResult.value?.data)

    }

    @Test
    fun `should clear all input fields when clearinput is called`() {
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
}