package com.example.speechbuddy.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Entry
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.repository.SymbolRepository
import com.example.speechbuddy.repository.WeightTableRepository
import com.example.speechbuddy.ui.models.DisplayMode
import com.example.speechbuddy.ui.models.SymbolItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
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
import org.junit.Rule
import org.junit.Test

class SymbolSelectionViewModelTest {
    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @MockK
    private val mockSymbolRepository: SymbolRepository = mockk(relaxed = true)
    private val mockWeightTableRepository: WeightTableRepository = mockk(relaxed = true)
    private lateinit var viewModel: SymbolSelectionViewModel

    private val symbol = Symbol(
        id = 1, text = "test1", imageUrl = null, categoryId = 1, isFavorite = false, isMine = false
    )
    private val category = Category(1, "category")
    private val query = "test"

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = SymbolSelectionViewModel(mockSymbolRepository, mockWeightTableRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    class TestObserver<T> : Observer<T> {
        val observedValues = mutableListOf<T?>()

        override fun onChanged(value: T) {
            observedValues.add(value)
        }
    }

    @Test
    fun `should enter display max when called`() {
        viewModel.enterDisplayMax()
        assertEquals(true, viewModel.uiState.value.isDisplayMax)
    }

    @Test
    fun `should exit display max when called`() {
        viewModel.exitDisplayMax()
        assertEquals(false, viewModel.uiState.value.isDisplayMax)
    }

    @Test
    fun `should select display mode when display mode is inputted`() {
        val displayModeSymbol = DisplayMode.SYMBOL
        val displayModeFavorite = DisplayMode.FAVORITE
        val displayModeAll = DisplayMode.ALL
        val displayModeCategory = DisplayMode.CATEGORY
        val observer = TestObserver<List<Entry>>()
        viewModel.entries.observeForever(observer)

        coEvery { mockSymbolRepository.getEntries("") } returns flowOf(listOf(symbol))
        coEvery { mockSymbolRepository.getSymbols("") } returns flowOf(listOf(symbol))
        coEvery { mockSymbolRepository.getCategories("") } returns flowOf(listOf(category))
        coEvery { mockSymbolRepository.getFavoriteSymbols("") } returns flowOf(listOf(symbol))

        viewModel.selectDisplayMode(displayModeSymbol)
        Thread.sleep(10) // viewModel.setQuery does not immediately produce result
        assertEquals(displayModeSymbol, viewModel.uiState.value.displayMode)
        assertEquals(symbol, observer.observedValues.last()?.get(0))

        viewModel.selectDisplayMode(displayModeFavorite)
        Thread.sleep(10) // viewModel.setQuery does not immediately produce result
        assertEquals(displayModeFavorite, viewModel.uiState.value.displayMode)
        assertEquals(symbol, observer.observedValues.last()?.get(0))

        viewModel.selectDisplayMode(displayModeAll)
        Thread.sleep(10) // viewModel.setQuery does not immediately produce result
        assertEquals(displayModeAll, viewModel.uiState.value.displayMode)
        assertEquals(symbol, observer.observedValues.last()?.get(0))

        viewModel.selectDisplayMode(displayModeCategory)
        Thread.sleep(10) // viewModel.setQuery does not immediately produce result
        assertEquals(displayModeCategory, viewModel.uiState.value.displayMode)
        assertEquals(category, observer.observedValues.last()?.get(0))
    }


    @Test
    fun `should set query when query is inputted`() = runBlocking {
        val observer = TestObserver<List<Entry>>()
        viewModel.entries.observeForever(observer)

        coEvery { mockSymbolRepository.getEntries(query) } returns flowOf(listOf(symbol))

        viewModel.setQuery(query)
        Thread.sleep(10) // viewModel.setQuery does not immediately produce result

        assertEquals(symbol, observer.observedValues.last()?.get(0))
        coVerify { mockSymbolRepository.getEntries(query) }
    }


    @Test
    fun `clear single symbol when symbol item is inputted`() {
        val symbolItem = SymbolItem(1, symbol)
        coEvery { mockSymbolRepository.getEntries("") } returns flowOf(listOf(symbol))
        coEvery { mockSymbolRepository.getSymbols("") } returns flowOf(listOf(symbol))
        coEvery { mockSymbolRepository.getCategories("") } returns flowOf(listOf(category))
        coEvery { mockSymbolRepository.getFavoriteSymbols("") } returns flowOf(listOf(symbol))
        coEvery { mockWeightTableRepository.provideSuggestion(symbol) } returns flowOf(listOf(symbol))

        viewModel.selectSymbol(symbol)
        viewModel.clear(symbolItem)
        assertEquals(symbol, viewModel.entries.value?.get(0))

        viewModel.clear(symbolItem)
        assertEquals(symbol, viewModel.entries.value?.get(0))
    }

    @Test
    fun `should clear all selected symbols when called`() {
        val selectedSymbols = listOf(SymbolItem(0, symbol))

        coEvery { mockWeightTableRepository.update(selectedSymbols) } returns Unit

        viewModel.selectSymbol(symbol)
        viewModel.clearAll()

        coVerify { mockWeightTableRepository.update(selectedSymbols) }
        assertEquals(true, viewModel.selectedSymbols.isEmpty())
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
    fun `should update weighttable and empty selected symbols when function is called`() =
        runBlocking {
            viewModel.selectSymbol(symbol)
            val temp = viewModel.selectedSymbols
            viewModel.clearAll()
            coVerify { mockWeightTableRepository.update(temp) }
            assertEquals(emptyList<SymbolItem>(), viewModel.selectedSymbols)
        }

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