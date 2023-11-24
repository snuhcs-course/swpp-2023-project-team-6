package com.example.speechbuddy.viewmodel

import android.content.Context
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.repository.SymbolRepository
import com.example.speechbuddy.ui.models.SymbolCreationErrorType
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
    private val sessionManager: SessionManager = mockk()
    val context: Context = mockk()
    private lateinit var viewModel: SymbolCreationViewModel

    private val emptySymbolText = ""
    private val longSymbolText = "tooLongSymbolTextToRegisterHahaToooooooLonggggg"
    private val validSymbolText = "valid"

    private val validCategory = Category(1, "validCategory")

    val validUri: Uri = mockk(relaxed = true)

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

    // setSymbolText-related
    @Test
    fun `should set empty symbol text before create click when setSymbolText is called with an empty text`() {
        viewModel.setSymbolText(emptySymbolText)

        assertEquals(validSymbolText, viewModel.symbolTextInput)
        assertEquals(false, viewModel.uiState.value.isValidSymbolText)
        assertEquals(null, viewModel.uiState.value.error?.type)
    }

    @Test
    fun `should set long symbol text before create click when setSymbolText is called with a long text`() {
        viewModel.setSymbolText(longSymbolText)

        assertEquals(validSymbolText, viewModel.symbolTextInput)
        assertEquals(false, viewModel.uiState.value.isValidSymbolText)
        assertEquals(null, viewModel.uiState.value.error?.type)
    }

    @Test
    fun `should set valid symbol text before create click when setSymbolText is called with a valid text`() {
        viewModel.setSymbolText(validSymbolText)

        assertEquals(validSymbolText, viewModel.symbolTextInput)
        assertEquals(false, viewModel.uiState.value.isValidSymbolText)
        assertEquals(null, viewModel.uiState.value.error?.type)
    }

    @Test
    fun `should set error after create click when setSymbolText is called with an empty text`() {
        viewModel.setSymbolText(emptySymbolText)
        viewModel.createSymbol(context)

        assertEquals(emptySymbolText, viewModel.symbolTextInput)
        assertEquals(false, viewModel.uiState.value.isValidSymbolText)
        assertEquals(SymbolCreationErrorType.SYMBOL_TEXT, viewModel.uiState.value.error?.type)
    }

    @Test
    fun `should set error after create click when setSymbolText is called with a long text`() {
        viewModel.setSymbolText(longSymbolText)
        viewModel.createSymbol(context)

        assertEquals(longSymbolText, viewModel.symbolTextInput)
        assertEquals(false, viewModel.uiState.value.isValidSymbolText)
        assertEquals(SymbolCreationErrorType.SYMBOL_TEXT, viewModel.uiState.value.error?.type)
    }

    @Test
    fun `should set no symbol text error after create click when empty symbol text is changed to a valid text`() {
        viewModel.setSymbolText(emptySymbolText)
        viewModel.createSymbol(context)
        viewModel.setSymbolText(validSymbolText)

        assertEquals(validSymbolText, viewModel.symbolTextInput)
        assertEquals(true, viewModel.uiState.value.isValidSymbolText)
        assertEquals(null, viewModel.uiState.value.error?.type)
    }

    @Test
    fun `should set no symbol text error after create click when long symbol text is changed to a valid text`() {
        viewModel.setSymbolText(longSymbolText)
        viewModel.createSymbol(context)
        viewModel.setSymbolText(validSymbolText)

        assertEquals(validSymbolText, viewModel.symbolTextInput)
        assertEquals(true, viewModel.uiState.value.isValidSymbolText)
        assertEquals(null, viewModel.uiState.value.error?.type)
    }

    @Test
    fun `should set non-symbol-text-error after create click when setSymbolText is called with a valid text`() {
        viewModel.setSymbolText(validSymbolText)
        viewModel.createSymbol(context)

        assertEquals(validSymbolText, viewModel.symbolTextInput)
        assertEquals(false, viewModel.uiState.value.isValidSymbolText)
        assertEquals(SymbolCreationErrorType.CATEGORY, viewModel.uiState.value.error?.type)
    }

    @Test
    fun `should set non-symbol-text-error after create click when valid text is changed to empty text`() {
        viewModel.setSymbolText(validSymbolText)
        viewModel.createSymbol(context)
        viewModel.setSymbolText(emptySymbolText)

        assertEquals(emptySymbolText, viewModel.symbolTextInput)
        assertEquals(false, viewModel.uiState.value.isValidSymbolText)
        assertEquals(SymbolCreationErrorType.CATEGORY, viewModel.uiState.value.error?.type)
    }

    // setCategory related
    @Test
    fun `should set no category when setCategory is not called`() {
        assertEquals(null, viewModel.categoryInput)
        assertEquals(false, viewModel.uiState.value.isValidCategory)
        assertEquals(null, viewModel.uiState.value.error?.type)
    }

    @Test
    fun `should set valid category before create click when setCategory is called with a valid category`() {
        viewModel.setCategory(validCategory)

        assertEquals(validCategory, viewModel.categoryInput)
        assertEquals(false, viewModel.uiState.value.isValidCategory)
        assertEquals(null, viewModel.uiState.value.error?.type)
    }

    @Test
    fun `should set error after create click when setCategory is not called`() {
        viewModel.setSymbolText(validSymbolText)
        viewModel.createSymbol(context)

        assertEquals(null, viewModel.categoryInput)
        assertEquals(false, viewModel.uiState.value.isValidCategory)
        assertEquals(SymbolCreationErrorType.CATEGORY, viewModel.uiState.value.error?.type)
    }

    // setPhoto related
    @Test
    fun `should set no photoInputUri when setPhoto is not called`() {
        assertEquals(null, viewModel.photoInputUri)
        assertEquals(false, viewModel.uiState.value.isValidPhotoInput)
        assertEquals(null, viewModel.uiState.value.error?.type)
    }

    @Test
    fun `should set error after create click when setPhoto is not called`() {
        viewModel.setSymbolText(validSymbolText)
        viewModel.setCategory(validCategory)
        viewModel.createSymbol(context)

        assertEquals(null, viewModel.photoInputUri)
        assertEquals(null, viewModel.photoInputBitmap)
        assertEquals(false, viewModel.uiState.value.isValidPhotoInput)
        assertEquals(SymbolCreationErrorType.PHOTO_INPUT, viewModel.uiState.value.error?.type)
    }

}
