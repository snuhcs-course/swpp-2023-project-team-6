package com.example.speechbuddy.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.repository.SymbolRepository
import com.example.speechbuddy.repository.WeightTableRepository
import com.example.speechbuddy.ui.models.MySymbolSettingsDisplayMode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MySymbolSettingsViewModelTest {
    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @MockK
    private val mockWeightTableRepository: WeightTableRepository = mockk(relaxed = true)
    private val mockSymbolRepository: SymbolRepository = mockk(relaxed = true)
    private lateinit var viewModel: MySymbolSettingsViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    val symbol = Symbol(1, "test", null, 1, false, true)
    val query = "test"


    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = MySymbolSettingsViewModel(mockWeightTableRepository, mockSymbolRepository)
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
    fun `should set query when called`() {
        viewModel.setQuery(query)

        assertEquals(query, viewModel.queryInput)
    }

    @Test
    fun `should toggle checked symbols when symbol is inputted`() {
        viewModel.toggleSymbolChecked(symbol)

        assertEquals(symbol, viewModel.checkedSymbols[0])
    }

    @Test
    fun `should un-toggle checked symbols when symbol is inputted`() {
        viewModel.toggleSymbolChecked(symbol)
        viewModel.toggleSymbolChecked(symbol)

        assertEquals(true, viewModel.checkedSymbols.isEmpty())
    }

    @Test
    fun `should update favorite when symbol is inputted`() = runBlocking {
        coEvery { mockSymbolRepository.updateFavorite(symbol, true) } returns Unit
        coEvery { mockSymbolRepository.updateFavorite(symbol, false) } returns Unit

        viewModel.updateFavorite(symbol, true)
        coVerify { mockSymbolRepository.updateFavorite(symbol, true) }

        viewModel.updateFavorite(symbol, false)
        coVerify { mockSymbolRepository.updateFavorite(symbol, false) }
    }

    @Test
    fun `should select display mode when display mode is inputted`() = runBlocking {
        val displayModeSymbol = MySymbolSettingsDisplayMode.SYMBOL
        val displayModeFavorite = MySymbolSettingsDisplayMode.FAVORITE
        val observer = TestObserver<List<Symbol>>()
        viewModel.symbols.observeForever(observer)

        coEvery { mockSymbolRepository.getUserSymbols("") } returns flowOf(listOf(symbol))
        coEvery { mockSymbolRepository.getFavoriteSymbols("") } returns flowOf(listOf(symbol))

        viewModel.selectDisplayMode(displayModeFavorite)
        Thread.sleep(10) // viewModel.selectDisplayMode() does not immediately produce result
        assertEquals(displayModeFavorite, viewModel.uiState.value.mySymbolSettingsDisplayMode)
        assertEquals(symbol, observer.observedValues.last()?.get(0))

        viewModel.selectDisplayMode(displayModeSymbol)
        Thread.sleep(10)
        assertEquals(displayModeSymbol, viewModel.uiState.value.mySymbolSettingsDisplayMode)
        assertEquals(symbol, observer.observedValues.last()?.get(0))
    }

    @Test
    fun `should delete checked symbols when called`() = runBlocking {
        coEvery { mockSymbolRepository.deleteSymbol(symbol) } returns Unit
        coEvery { mockWeightTableRepository.updateWeightTableForDeletedSymbol(symbol) }returns Unit

        viewModel.toggleSymbolChecked(symbol)
        viewModel.deleteCheckedSymbols()

        coVerify { mockSymbolRepository.deleteSymbol(symbol) }
        coVerify { mockWeightTableRepository.updateWeightTableForDeletedSymbol(symbol) }
        assertEquals(true, viewModel.checkedSymbols.isEmpty())
    }
}