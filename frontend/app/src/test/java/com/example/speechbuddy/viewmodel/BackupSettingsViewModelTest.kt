package com.example.speechbuddy.viewmodel

import com.example.speechbuddy.repository.SettingsRepository
import com.example.speechbuddy.ui.models.BackupSettingsAlert
import com.example.speechbuddy.ui.models.BackupSettingsUiState
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
import org.junit.Test
import retrofit2.Response
import java.time.LocalDate

class BackupSettingsViewModelTest {
    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @MockK
    private lateinit var mockSettingsRepository: SettingsRepository
    private lateinit var viewModel: BackupSettingsViewModel


    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        mockSettingsRepository = mockk(relaxed = true)
        viewModel = BackupSettingsViewModel(mockSettingsRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `should set autoBackup when called`() = runBlocking {
        coEvery { mockSettingsRepository.setAutoBackup(true) } returns Unit
        viewModel.setAutoBackup(true)
        assertEquals(BackupSettingsUiState(isAutoBackupEnabled = true), viewModel.uiState.value)
        coVerify { mockSettingsRepository.setAutoBackup(true) }

        coEvery { mockSettingsRepository.setAutoBackup(false) } returns Unit
        viewModel.setAutoBackup(false)
        assertEquals(BackupSettingsUiState(isAutoBackupEnabled = false), viewModel.uiState.value)
        coVerify { mockSettingsRepository.setAutoBackup(false) }
    }

    @Test
    fun `should show toast when called`() = runBlocking {
        viewModel.toastDisplayed()

        assertEquals(
            BackupSettingsUiState(
                alert = null,
                isAutoBackupEnabled = false,
                loading = false,
                buttonEnabled = true
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should run backup when called`() = runBlocking {
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
        val expectedValue1 = BackupSettingsUiState(
            isAutoBackupEnabled = false,
            loading = !viewModel.uiState.value.loading,
            buttonEnabled = !viewModel.uiState.value.buttonEnabled
        )

        viewModel.backup()

        val observedUiStates = viewModel.uiState.take(1).toList()
        val expectedValue2 = BackupSettingsUiState(
            lastBackupDate = date,
            isAutoBackupEnabled = false,
            loading = !expectedValue1.loading,
            buttonEnabled = !expectedValue1.buttonEnabled,
            alert = BackupSettingsAlert.SUCCESS
        )

        assertEquals(expectedValue1, observedUiStates)
        coVerify { mockSettingsRepository.setLastBackupDate(date) }
    }


}