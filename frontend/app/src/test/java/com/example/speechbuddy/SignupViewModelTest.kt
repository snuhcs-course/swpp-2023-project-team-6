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
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

//@RunWith(JUnit4::class)
class SignupViewModelTest {
    /*
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



    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    @Rule
    fun setup() {
        //Dispatchers.setMain(mainThreadSurrogate)
        viewModel = SignupViewModel(repository)
        //val instantTaskExecutorRule = InstantTaskExecutorRule()
    }

    @After
    fun tearDown() {
        //Dispatchers.resetMain()
        //mainThreadSurrogate.close()
    }

    @Test
    fun signupViewModel_setNickname_empty_beforeSignupClick() {
        viewModel.setNickname(emptyNickname)

        assertEquals(emptyNickname, viewModel.nicknameInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidNickname, false)
    }

    @Test
    fun signupViewModel_setNickname_long_beforeSignupClick() {
        viewModel.setNickname(longNickname)

        assertEquals(longNickname, viewModel.nicknameInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidNickname, false)
    }

    @Test
    fun signupViewModel_setNickname_valid_beforeSignupClick() {
        viewModel.setNickname(validNickname)

        assertEquals(validNickname, viewModel.nicknameInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidNickname, false)
    }

    @Test
    fun signupViewModel_setNickname_empty_afterSignupClick(){
        viewModel.setNickname(emptyNickname)

        viewModel.signup(validEmail)

        assertEquals(emptyNickname, viewModel.nicknameInput)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.NICKNAME)
        assertEquals(viewModel.uiState.value.isValidNickname, false)

        viewModel.setNickname(validNickname)

        assertEquals(validNickname, viewModel.nicknameInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidNickname, true)
    }

    @Test
    fun signupViewModel_setNickname_long_afterSignupClick(){
        viewModel.setNickname(longNickname)

        viewModel.signup(validEmail)

        assertEquals(longNickname, viewModel.nicknameInput)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.NICKNAME)
        assertEquals(viewModel.uiState.value.isValidNickname, false)

        viewModel.setNickname(validNickname)

        assertEquals(validNickname, viewModel.nicknameInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidNickname, true)
    }

    @Test
    fun signupViewModel_setNickname_valid_afterSignupClick(){
        viewModel.setNickname(validNickname)

        viewModel.signup("email@test.com")

        assertEquals(validNickname, viewModel.nicknameInput)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.PASSWORD)
        assertEquals(viewModel.uiState.value.isValidNickname, false)

        viewModel.setNickname(longNickname)

        assertEquals(longNickname, viewModel.nicknameInput)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.PASSWORD)
        assertEquals(viewModel.uiState.value.isValidNickname, false)
    }

    @Test
    fun signupViewModel_setPassword_short_beforeSignupClick(){
        viewModel.setPassword(shortPassword)

        assertEquals(shortPassword, viewModel.passwordInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidPassword, false)
    }

    @Test
    fun signupViewModel_setPassword_valid_beforeSignupClick(){
        viewModel.setPassword(validPassword)

        assertEquals(validPassword, viewModel.passwordInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidPassword, false)
    }

    @Test
    fun signupViewModel_setPassword_short_afterSignupClick(){
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
    fun signupViewModel_setPassword_valid_afterSignupClick(){
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
    fun signupViewModel_setPasswordCheck_invalid_beforeSignupClick(){
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(shortPassword)

        assertEquals(shortPassword, viewModel.passwordCheckInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
    }

    @Test
    fun signupViewModel_setPasswordCheck_valid_beforeSignupClick(){
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(validPassword)

        assertEquals(validPassword, viewModel.passwordCheckInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
    }

    @Test
    fun signupViewModel_setPasswordCheck_invalid_afterSignupClick(){
        viewModel.setPassword(validPassword)
        viewModel.setNickname(validNickname)
        viewModel.setPasswordCheck(shortPassword)

        viewModel.signup(validEmail)

        assertEquals(shortPassword, viewModel.passwordCheckInput)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.PASSWORD_CHECK)
        assertEquals(viewModel.uiState.value.isValidEmail, false)

        viewModel.setPasswordCheck(validPassword)

        assertEquals(validPassword, viewModel.passwordCheckInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidEmail, true)
    }

    @Test
    fun signupViewModel_setPasswordCheck_valid_afterSignupClick(){
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(validPassword)

        viewModel.signup(validEmail)

        assertEquals(validPassword, viewModel.passwordCheckInput)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.NICKNAME)
        assertEquals(viewModel.uiState.value.isValidPassword, false)

        viewModel.setPasswordCheck(shortPassword)

        assertEquals(shortPassword, viewModel.passwordCheckInput)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.NICKNAME)
        assertEquals(viewModel.uiState.value.isValidPassword, false)
    }

    @Test
    fun signupViewModel_signup_emptyNickname_shortPwd_invalidPwdCheck(){
        viewModel.setNickname(emptyNickname)
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
    fun signupViewModel_signup_emptyNickname_shortPwd_validPwdCheck(){
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
    fun signupViewModel_signup_emptyNickname_validPwd_invalidPwdCheck(){
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
    fun signupViewModel_signup_emptyNickname_validPwd_validPwdCheck(){
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
    fun signupViewModel_signup_longNickname_shortPwd_invalidPwdCheck(){
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
    fun signupViewModel_signup_longNickname_shortPwd_validPwdCheck(){
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
    fun signupViewModel_signup_longNickname_validPwd_invalidPwdCheck(){
        viewModel.setNickname(longNickname)
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
    fun signupViewModel_signup_longNickname_validPwd_validPwdCheck(){
        viewModel.setNickname(longNickname)
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
    fun signupViewModel_signup_validNickname_shortPwd_invalidPwdCheck(){
        viewModel.setNickname(validNickname)
        viewModel.setPassword(shortPassword)
        viewModel.setPasswordCheck(validPassword)

        viewModel.signup(validEmail)

        assertEquals(viewModel.uiState.value.isValidNickname, false)
        assertEquals(viewModel.uiState.value.isValidPassword, false)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.PASSWORD)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.false_new_password)
    }

    @Test
    fun signupViewModel_signup_validNickname_shortPwd_validPwdCheck(){
        viewModel.setNickname(validNickname)
        viewModel.setPassword(shortPassword)
        viewModel.setPasswordCheck(shortPassword)

        viewModel.signup(validEmail)

        assertEquals(viewModel.uiState.value.isValidNickname, false)
        assertEquals(viewModel.uiState.value.isValidPassword, false)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.PASSWORD)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.false_new_password)
    }

    @Test
    fun signupViewModel_signup_validNickname_validPwd_invalidPwdCheck(){
        viewModel.setNickname(validNickname)
        viewModel.setPassword(validPassword)
        viewModel.setPasswordCheck(shortPassword)

        viewModel.signup(validEmail)

        assertEquals(viewModel.uiState.value.isValidNickname, false)
        assertEquals(viewModel.uiState.value.isValidPassword, false)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
        assertEquals(viewModel.uiState.value.error?.type, SignupErrorType.PASSWORD_CHECK)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.false_new_password_check)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun signupViewModel_signup_validNickname_validPwd_validPwdCheck() = runTest{
        val authSignupRequest = AuthSignupRequest(validEmail, validPassword, validNickname)
        launch(Dispatchers.Main){
            coEvery { repository.signup(authSignupRequest) } returns flowOf(Resource.success(null))

            viewModel.setNickname(validNickname)
            viewModel.setPassword(validPassword)
            viewModel.setPasswordCheck(validPassword)

            viewModel.signup(validEmail)
        }

        assertEquals(viewModel.signupResult.value?.message, null)
        assertEquals(viewModel.signupResult.value?.data, null)

    }
     */

}