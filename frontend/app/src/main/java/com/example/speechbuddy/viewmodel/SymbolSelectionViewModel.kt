package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Entry
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.repository.SymbolRepository
import com.example.speechbuddy.repository.WeightTableRepository
import com.example.speechbuddy.ui.models.DisplayMode
import com.example.speechbuddy.ui.models.SymbolItem
import com.example.speechbuddy.ui.models.SymbolSelectionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SymbolSelectionViewModel @Inject internal constructor(
    private val repository: SymbolRepository,
    private val weightTableRepository: WeightTableRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SymbolSelectionUiState())
    val uiState: StateFlow<SymbolSelectionUiState> = _uiState.asStateFlow()

    var queryInput by mutableStateOf("")
        private set

    var selectedSymbols by mutableStateOf(listOf<SymbolItem>())
        private set

    private var selectedCategory by mutableStateOf<Category?>(null)

    private val _entries = MutableLiveData<List<Entry>>()
    val entries: LiveData<List<Entry>> get() = _entries

    private var getEntriesJob: Job? = null
    private var needsToBeRecalled: Boolean = false

    init {
        repository.checkImages()
        getEntries()
    }

    fun enterDisplayMax() {
        _uiState.update { currentState ->
            currentState.copy(
                isDisplayMax = true
            )
        }
    }

    fun exitDisplayMax() {
        _uiState.update { currentState ->
            currentState.copy(
                isDisplayMax = false
            )
        }
    }

    fun selectDisplayMode(displayMode: DisplayMode) {
        _uiState.update { currentState ->
            currentState.copy(
                displayMode = displayMode
            )
        }
        queryInput = ""
        getEntries()
    }

    fun setQuery(input: String) {
        queryInput = input
        getEntries()
    }

    fun clear(symbolItem: SymbolItem) {
        selectedSymbols = selectedSymbols.minus(symbolItem)
        if (selectedSymbols.isEmpty())
            getEntries()
        else
            provideSuggestion(selectedSymbols.last().symbol)
    }

    fun clearAll() {
        weightTableRepository.update(selectedSymbols)
        selectedSymbols = emptyList()
        getEntries()
    }

    fun selectSymbol(symbol: Symbol): Int {
        queryInput = ""

        val newSymbolItem = SymbolItem(id = selectedSymbols.size, symbol = symbol)
        selectedSymbols = selectedSymbols.plus(newSymbolItem)

        _uiState.update { currentState ->
            currentState.copy(
                displayMode = DisplayMode.SYMBOL
            )
        }

        provideSuggestion(symbol)

        return newSymbolItem.id
    }

    fun updateFavorite(symbol: Symbol, value: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(symbol, value)
        }
        if (needsToBeRecalled) {
            /**
             * selectedSymbols should always be NOT EMPTY in this case,
             * thus executing provideSuggestion() as expected.
             */
            if (selectedSymbols.isEmpty())
                getEntries()
            else
                provideSuggestion(selectedSymbols.last().symbol)
        }
    }

    fun selectCategory(category: Category) {
        if (category != selectedCategory) {
            selectedCategory = category
            getEntriesJob?.cancel()
            needsToBeRecalled = false
            getEntriesJob = viewModelScope.launch {
                repository.getSymbolsByCategory(category).collect { symbols ->
                    _entries.postValue(listOf(category) + symbols)
                }
            }
        } else {
            selectedCategory = null
            getEntries()
        }
    }

    private fun provideSuggestion(symbol: Symbol) {
        /**
         * became independent from selectSymbol function
         * change it so that providing suggestion is available from any screen
         */
        getEntriesJob?.cancel()
        needsToBeRecalled = true
        getEntriesJob = viewModelScope.launch {
            weightTableRepository.provideSuggestion(symbol).collect { symbols ->
                _entries.postValue(symbols)
            }
        }
    }

    private fun getEntries() {
        getEntriesJob?.cancel()
        needsToBeRecalled = false
        getEntriesJob = viewModelScope.launch {
            when (_uiState.value.displayMode) {
                DisplayMode.ALL -> {
                    repository.getEntries(queryInput).collect { entries ->
                        _entries.postValue(entries)
                    }
                }

                DisplayMode.SYMBOL -> {
                    repository.getSymbols(queryInput).collect { symbols ->
                        _entries.postValue(symbols)
                    }
                }

                DisplayMode.CATEGORY -> {
                    repository.getCategories(queryInput).collect { categories ->
                        _entries.postValue(categories)
                    }
                }

                DisplayMode.FAVORITE -> {
                    repository.getFavoriteSymbols(queryInput).collect { symbols ->
                        _entries.postValue(symbols)
                    }
                }
            }
        }
    }

}