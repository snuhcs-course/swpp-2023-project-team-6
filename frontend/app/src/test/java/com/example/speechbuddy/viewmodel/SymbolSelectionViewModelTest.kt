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

    val symbol1 = Symbol(
        id = 1,
        text = "test1",
        imageUrl = null,
        categoryId = 1,
        isFavorite = false,
        isMine = false
    )
    val symbol2 = Symbol(
        id = 2,
        text = "test2",
        imageUrl = null,
        categoryId = 1,
        isFavorite = false,
        isMine = false
    )
    val symbol3 = Symbol(
        id = 3,
        text = "test3",
        imageUrl = null,
        categoryId = 1,
        isFavorite = false,
        isMine = false
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
            viewModel.selectSymbol(symbol1)

            assertEquals(listOf(SymbolItem(0, symbol1)), viewModel.selectedSymbols)
            coVerify { mockWeightTableRepository.provideSuggestion(symbol1) }
        }

//    @Test
//    fun `should clear last symbol when function is called`() = runBlocking {
//        viewModel.selectSymbol(symbol1)
//        viewModel.selectSymbol(symbol2)
//        viewModel.selectSymbol(symbol3)
//
//        val mockSymbolItem = SymbolItem(3, symbol3)
//
//        viewModel.clear(mockSymbolItem)
//
//        coVerify { mockWeightTableRepository.provideSuggestion(symbol2) }
//    }


    @Test
    fun `should set isMenuExpanded to true when function is called`() = runBlocking {
        // Given
        val initialUiState = viewModel.uiState.first()

        // When
        viewModel.expandMenu()

        // Then
        val updatedUiState = viewModel.uiState.first()
        assertEquals(true, updatedUiState.isMenuExpanded)
        assertEquals(initialUiState.copy(isMenuExpanded = true), updatedUiState)
    }

    @Test
    fun `should set isMenuExpanded to false when function is called`() = runBlocking {
        // Given
        val initialUiState = viewModel.uiState.first()

        // When
        viewModel.dismissMenu()

        // Then
        val updatedUiState = viewModel.uiState.first()
        assertEquals(false, updatedUiState.isMenuExpanded)
        assertEquals(initialUiState.copy(isMenuExpanded = false), updatedUiState)
    }

    @Test
    fun `should set displayMode and isMenuExpanded when selectDisplayMode is called`() =
        runBlocking {
            // Given
            val initialUiState = viewModel.uiState.first()
            val displayModeList =
                listOf(
                    DisplayMode.ALL,
                    DisplayMode.SYMBOL,
                    DisplayMode.CATEGORY,
                    DisplayMode.FAVORITE
                )
            val mockSymbols = listOf(Symbol(1, "symbol1", "desc1", 1, false, isMine = false))
            coEvery { mockSymbolRepository.getEntries("") } returns flowOf(mockSymbols)

            for (displayMode in displayModeList) {

                // When
                viewModel.selectDisplayMode(displayMode)

                // Then
                val updatedUiState = viewModel.uiState.first()
                assertEquals(displayMode, updatedUiState.displayMode)
                assertEquals(false, updatedUiState.isMenuExpanded)
                assertEquals(
                    initialUiState.copy(isMenuExpanded = false, displayMode = displayMode),
                    updatedUiState
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
//            viewModel.selectSymbol(symbol1)
//            val temp = viewModel.selectedSymbols
//            viewModel.clearAll()
//            coVerify { mockWeightTableRepository.update(temp) }
//            assertEquals(emptyList<SymbolItem>(), viewModel.selectedSymbols)
//        }

    @Test
    fun `should update favorite when function is called`() = runBlocking {
        viewModel.updateFavorite(symbol1, true)
        coVerify { mockSymbolRepository.updateFavorite(symbol1, true) }
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