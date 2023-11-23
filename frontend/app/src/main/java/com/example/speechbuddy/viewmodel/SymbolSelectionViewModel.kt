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

    init {
        getEntries()
        repository.checkImages()
    }

    fun expandMenu() {
        _uiState.update { currentState ->
            currentState.copy(
                isMenuExpanded = true
            )
        }
    }

    fun dismissMenu() {
        _uiState.update { currentState ->
            currentState.copy(
                isMenuExpanded = false
            )
        }
    }

    fun selectDisplayMode(displayMode: DisplayMode) {
        _uiState.update { currentState ->
            currentState.copy(
                isMenuExpanded = false, displayMode = displayMode
            )
        }
        getEntries()
    }

    fun setQuery(input: String) {
        queryInput = input
        /**
         * Passes a new queryInput to getEntries() to ensure that
         * getEntries() is called precisely because of a change in query
         */
        getEntries(input)
    }

    fun clear(symbolItem: SymbolItem) {
        selectedSymbols = selectedSymbols.minus(symbolItem)
        // when clearing one left selected symbol
        if (selectedSymbols.isNotEmpty()) {
            val lastSelectedSymbol = selectedSymbols.last()
            provideSuggestion(lastSelectedSymbol.symbol)
        }
    }

    fun clearAll() {
        weightTableRepository.update(selectedSymbols)
        selectedSymbols = emptyList()
    }

    fun selectSymbol(symbol: Symbol) {
        queryInput = ""
        selectedSymbols =
            selectedSymbols.plus(SymbolItem(id = selectedSymbols.size, symbol = symbol))

        provideSuggestion(symbol)
    }

    fun updateFavorite(symbol: Symbol, value: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(symbol, value)
        }
    }

    fun selectCategory(category: Category) {
        if (category != selectedCategory) {
            selectedCategory = category
            getEntriesJob?.cancel()
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
        // became independent from selectSymbol function
        // change it so that providing suggestion is available from any screen
//        if (uiState.value.displayMode == DisplayMode.SYMBOL) {

        getEntriesJob?.cancel()
        getEntriesJob = viewModelScope.launch {
            weightTableRepository.provideSuggestion(symbol).collect { symbols ->
                _entries.postValue(symbols)
            }
        }
//        }
    }

    private fun getEntries(query: String? = null) {
        getEntriesJob?.cancel()
        getEntriesJob = viewModelScope.launch {
            when (_uiState.value.displayMode) {
                DisplayMode.ALL -> {
                    repository.getEntries(queryInput).collect { entries ->
                        _entries.postValue(entries)
                    }
                }

                /**
                 * In case of DisplayMode.SYMBOL and DisplayMode.CATEGORY,
                 * if getEntries() is called by setQuery(),
                 * retrieve both symbols and categories from the repository
                 */
                DisplayMode.SYMBOL -> {
                    if (query != null) // called from setQuery()
                        repository.getEntries(query).collect { entries ->
                            _entries.postValue(entries)
                        }
                    else // called from somewhere else
                        repository.getSymbols(queryInput).collect { symbols ->
                            _entries.postValue(symbols)
                        }
                }

                DisplayMode.CATEGORY -> {
                    if (query != null)
                        repository.getEntries(query).collect { entries ->
                            _entries.postValue(entries)
                        }
                    else
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