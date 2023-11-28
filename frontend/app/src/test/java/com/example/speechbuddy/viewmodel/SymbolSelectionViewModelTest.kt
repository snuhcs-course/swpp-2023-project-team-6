package com.example.speechbuddy.viewmodel

import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.repository.SymbolRepository
import com.example.speechbuddy.repository.WeightTableRepository
import com.example.speechbuddy.ui.models.DisplayMode
import com.example.speechbuddy.ui.models.SymbolItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SymbolSelectionViewModelTest {
    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var viewModel: SymbolSelectionViewModel
    private lateinit var mockSymbolRepository: SymbolRepository
    private lateinit var mockWeightTableRepository: WeightTableRepository

    private val symbol = Symbol(
        id = 1, text = "test1", imageUrl = null, categoryId = 1, isFavorite = false, isMine = false
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)

        mockSymbolRepository = mockk()
        mockWeightTableRepository = mockk()
        viewModel = SymbolSelectionViewModel(mockSymbolRepository, mockWeightTableRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `should add symbol to selectedSymbols and provide suggestion when symbol is given`() =
        runBlocking {
            viewModel.selectSymbol(symbol)

            assertEquals(listOf(SymbolItem(0, symbol)), viewModel.selectedSymbols)
            coVerify { mockWeightTableRepository.provideSuggestion(symbol) }
        }

    @Test
    fun `should set displayMode when selectDisplayMode is called`() = runBlocking {
        // Given
        val initialUiState = viewModel.uiState.first()
        val displayModeList = listOf(
            DisplayMode.ALL, DisplayMode.SYMBOL, DisplayMode.CATEGORY, DisplayMode.FAVORITE
        )
        val mockSymbols = listOf(Symbol(1, "symbol", "desc1", 1, false, isMine = false))
        coEvery { mockSymbolRepository.getEntries("") } returns flowOf(mockSymbols)

        for (displayMode in displayModeList) {

            // When
            viewModel.selectDisplayMode(displayMode)

            // Then
            val updatedUiState = viewModel.uiState.first()
            assertEquals(displayMode, updatedUiState.displayMode)
            assertEquals(
                initialUiState.copy(displayMode = displayMode), updatedUiState
            )

            // Verify that repository was queried
            coVerify { mockSymbolRepository.getEntries("") }
        }
    }

    @Test
    fun `should set setQuery when input is made`() = runBlocking {
        val input = "test"
        viewModel.setQuery(input)
        coVerify { mockSymbolRepository.getEntries(input) }
    }


//    @Test
//    fun `should update weighttable and empty selected symbols when function is called`() =
//        runBlocking {
//            viewModel.selectSymbol(symbol)
//            val temp = viewModel.selectedSymbols
//            viewModel.clearAll()
//            coVerify { mockWeightTableRepository.update(temp) }
//            assertEquals(emptyList<SymbolItem>(), viewModel.selectedSymbols)
//        }

    @Test
    fun `should update favorite when function is called`() = runBlocking {
        viewModel.updateFavorite(symbol, true)
        coVerify { mockSymbolRepository.updateFavorite(symbol, true) }
    }

    @Test
    fun `should show category when category is inputted`() = runBlocking {
        val category = Category(1, "test")
        viewModel.selectCategory(category)
        coVerify { mockSymbolRepository.getSymbolsByCategory(category) }
    }

    @Test
    fun `should show entries when category inputted is same as before`() = runBlocking {
        val category = Category(1, "test")
        viewModel.selectCategory(category)
        viewModel.selectCategory(category)
        coVerify { mockSymbolRepository.getCategories("") }
    }

}