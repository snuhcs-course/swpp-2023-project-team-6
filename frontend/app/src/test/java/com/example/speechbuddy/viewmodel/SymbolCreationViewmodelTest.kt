

package com.example.speechbuddy.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.repository.SymbolRepository
import com.example.speechbuddy.ui.models.SymbolCreationErrorType
import com.example.speechbuddy.ui.models.SymbolCreationUiState
import com.example.speechbuddy.utils.Resource
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SymbolCreationViewModelTest {

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @MockK
    private val repository: SymbolRepository = mockk()
    private val sessionManager : SessionManager = mockk()
    private lateinit var viewModel: SymbolCreationViewModel

    private val validSymbolText = "valid"

    private val invalidSymbolText = ""

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        coEvery { repository.getAllCategories() } returns flowOf(emptyList())
        viewModel = SymbolCreationViewModel(repository, sessionManager)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `should set valid symbol text when setSymbolText is called with a valid text`() {
        viewModel.setSymbolText(validSymbolText)

        assertEquals(validSymbolText, viewModel.symbolTextInput)
        assertEquals(false, viewModel.uiState.value.isValidSymbolText)
        assertEquals(null, viewModel.uiState.value.error?.type)
    }

    @Test
    fun `should set invalid symbol text when setSymbolText is called with invalid text`() {
        viewModel.setSymbolText(invalidSymbolText)

        assertEquals(invalidSymbolText, viewModel.symbolTextInput)
        assertEquals(false, viewModel.uiState.value.isValidSymbolText)
        assertEquals(null, viewModel.uiState.value.error?.type)
    }
}

