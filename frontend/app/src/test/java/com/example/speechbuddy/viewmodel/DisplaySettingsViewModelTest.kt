package com.example.speechbuddy.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.speechbuddy.repository.SettingsRepository
import com.example.speechbuddy.ui.models.DisplaySettingsUiState
import com.example.speechbuddy.ui.models.InitialPage
import com.example.speechbuddy.utils.Resource
import com.example.speechbuddy.utils.Status
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DisplaySettingsViewModelTest {
    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @MockK
    private val mockSettingsRepository: SettingsRepository = mockk(relaxed = true)
    private lateinit var viewModel: DisplaySettingsViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = DisplaySettingsViewModel(mockSettingsRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `should set dark mode when called`() = runBlocking {
        coEvery { mockSettingsRepository.setDarkMode(true) } returns Unit
        viewModel.setDarkMode(true)
        assertEquals(DisplaySettingsUiState(isDarkModeEnabled = true), viewModel.uiState.value)

        viewModel.setDarkMode(false)
        assertEquals(DisplaySettingsUiState(isDarkModeEnabled = false), viewModel.uiState.value)
    }

    @Test
    fun `should set initial page when called`() = runBlocking {
        coEvery { mockSettingsRepository.setInitialPage(InitialPage.SYMBOL_SELECTION) } returns Unit
        coEvery { mockSettingsRepository.setInitialPage(InitialPage.TEXT_TO_SPEECH) } returns Unit

        viewModel.setInitialPage(InitialPage.SYMBOL_SELECTION)
        assertEquals(
            DisplaySettingsUiState(initialPage = InitialPage.SYMBOL_SELECTION),
            viewModel.uiState.value
        )

        viewModel.setInitialPage(InitialPage.TEXT_TO_SPEECH)
        assertEquals(
            DisplaySettingsUiState(initialPage = InitialPage.TEXT_TO_SPEECH),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should return dark mode setting when called`() = runBlocking {
        val actualValue = viewModel.getDarkMode()
        assertEquals(false, actualValue)
    }

    @Test
    fun `should return initial page when called`() = runBlocking {
        val actualValue = viewModel.getInitialPage()
        assertEquals(true, actualValue)
    }

}