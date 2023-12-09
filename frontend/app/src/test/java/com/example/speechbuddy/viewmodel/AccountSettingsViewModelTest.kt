package com.example.speechbuddy.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.repository.SettingsRepository
import com.example.speechbuddy.repository.SymbolRepository
import com.example.speechbuddy.repository.UserRepository
import com.example.speechbuddy.repository.WeightTableRepository
import com.example.speechbuddy.ui.models.AccountSettingsAlert
import com.example.speechbuddy.ui.models.AccountSettingsUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.time.LocalDate

class AccountSettingsViewModelTest {
    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @MockK
    private val mockAuthRepository: AuthRepository = mockk(relaxed = true)
    private val mockSettingsRepository: SettingsRepository = mockk(relaxed = true)
    private val mockWeightTableRepository: WeightTableRepository = mockk(relaxed = true)
    private val mockSymbolRepository: SymbolRepository = mockk(relaxed = true)
    private val mockUserRepository: UserRepository = mockk(relaxed = true)
    private val mockSessionManager: SessionManager = mockk(relaxed = true)

    private lateinit var viewModel: AccountSettingsViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)

        viewModel = AccountSettingsViewModel(
            authRepository = mockAuthRepository,
            settingsRepository = mockSettingsRepository,
            weightTableRepository = mockWeightTableRepository,
            symbolRepository = mockSymbolRepository,
            userRepository = mockUserRepository,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `should show alert when called`() = runBlocking {
        var alert = AccountSettingsAlert.BACKUP
        var expectedUiState = AccountSettingsUiState(alert = alert, buttonEnabled = false)
        viewModel.showAlert(alert)
        var actualUiState = viewModel.uiState.value
        assertEquals(expectedUiState, actualUiState)

        alert = AccountSettingsAlert.LOADING
        expectedUiState = AccountSettingsUiState(alert = alert, buttonEnabled = false)
        viewModel.showAlert(alert)
        actualUiState = viewModel.uiState.value
        assertEquals(expectedUiState, actualUiState)

        alert = AccountSettingsAlert.BACKUP_SUCCESS
        expectedUiState = AccountSettingsUiState(alert = alert, buttonEnabled = false)
        viewModel.showAlert(alert)
        actualUiState = viewModel.uiState.value
        assertEquals(expectedUiState, actualUiState)

        alert = AccountSettingsAlert.LOGOUT
        expectedUiState = AccountSettingsUiState(alert = alert, buttonEnabled = false)
        viewModel.showAlert(alert)
        actualUiState = viewModel.uiState.value
        assertEquals(expectedUiState, actualUiState)

        alert = AccountSettingsAlert.WITHDRAW
        expectedUiState = AccountSettingsUiState(alert = alert, buttonEnabled = false)
        viewModel.showAlert(alert)
        actualUiState = viewModel.uiState.value
        assertEquals(expectedUiState, actualUiState)

        alert = AccountSettingsAlert.WITHDRAW_PROCEED
        expectedUiState = AccountSettingsUiState(alert = alert, buttonEnabled = false)
        viewModel.showAlert(alert)
        actualUiState = viewModel.uiState.value
        assertEquals(expectedUiState, actualUiState)

        alert = AccountSettingsAlert.CONNECTION
        expectedUiState = AccountSettingsUiState(alert = alert, buttonEnabled = false)
        viewModel.showAlert(alert)
        actualUiState = viewModel.uiState.value
        assertEquals(expectedUiState, actualUiState)
    }

    @Test
    fun `should hideAlert when called`() = runBlocking {
        viewModel.hideAlert()

        val expectedUiState = AccountSettingsUiState(alert = null)
        val actualUiState = viewModel.uiState.value

        assertEquals(expectedUiState, actualUiState)
    }

    @Test
    fun `should call authRepository when logout is called`() = runBlocking {
        coEvery { mockAuthRepository.logout() } returns flowOf(Response.success(null))

        viewModel.logout()

        val observedUiStates = viewModel.uiState.take(2).toList()
        val expectedValue1 =
            AccountSettingsUiState(alert = AccountSettingsAlert.LOADING, buttonEnabled = false)
        val expectedValue2 = AccountSettingsUiState(alert = null, buttonEnabled = true)
        coVerify { mockAuthRepository.logout() }
        assertEquals(listOf(expectedValue1, expectedValue2), observedUiStates)

    }

    @Test
    fun `should call userRepository when withdraw is called`() = runBlocking {
        coEvery { mockAuthRepository.withdraw() } returns flowOf(Response.success(null))

        viewModel.withdraw()

        val observedUiStates = viewModel.uiState.take(2).toList()
        val expectedValue1 =
            AccountSettingsUiState(alert = AccountSettingsAlert.LOADING, buttonEnabled = false)
        val expectedValue2 = AccountSettingsUiState(alert = null, buttonEnabled = true)
        coVerify { mockAuthRepository.withdraw() }
        assertEquals(listOf(expectedValue1, expectedValue2), observedUiStates)
    }

    @Test
    fun `should call authRepository when exitGuestMode is called`() = runBlocking {
        coEvery { mockSettingsRepository.resetSettings() } returns Unit
        coEvery { mockWeightTableRepository.resetAllWeightRows() } returns Unit
        coEvery { mockSymbolRepository.resetSymbolsAndFavorites() } returns Unit
        coEvery { mockUserRepository.deleteUserInfo() } returns Unit

        viewModel.exitGuestMode()

        coVerify { mockSettingsRepository.resetSettings() }
        coVerify { mockWeightTableRepository.resetAllWeightRows() }
        coVerify { mockSymbolRepository.resetSymbolsAndFavorites() }
        coVerify { mockUserRepository.deleteUserInfo() }
    }

    @Test
    fun `should updates uiState and calls settingsRepository when backup is called`() =
        runBlocking {
            val date = LocalDate.now().toString()
            coEvery { mockSettingsRepository.displayBackup() } returns flowOf(Response.success(null))
            coEvery { mockSettingsRepository.symbolListBackup() } returns flowOf(
                Response.success(
                    null
                )
            )
            coEvery { mockSettingsRepository.favoriteSymbolBackup() } returns flowOf(
                Response.success(
                    null
                )
            )
            coEvery { mockSettingsRepository.weightTableBackup() } returns flowOf(
                Response.success(
                    null
                )
            )

            viewModel.backup()

            val observedUiStates = viewModel.uiState.take(1).toList()
            val expectedValue1 =
                AccountSettingsUiState(alert = AccountSettingsAlert.LOADING, buttonEnabled = false)
            coVerify { mockSettingsRepository.setLastBackupDate(date) }
            assertEquals(listOf(expectedValue1), observedUiStates)

        }
}