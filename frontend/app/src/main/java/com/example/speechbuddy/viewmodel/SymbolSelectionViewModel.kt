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
import com.example.speechbuddy.ui.models.DisplayMode
import com.example.speechbuddy.ui.models.SymbolItem
import com.example.speechbuddy.ui.models.SymbolSelectionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SymbolSelectionViewModel @Inject internal constructor(
    private val repository: SymbolRepository
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

    init {
        viewModelScope.launch {
            getEntries()
        }
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
        viewModelScope.launch {
            getEntries()
        }
    }

    fun setQuery(input: String) {
        queryInput = input
        viewModelScope.launch {
            getEntries()
        }
    }

    fun clear(symbolItem: SymbolItem) {
        selectedSymbols = selectedSymbols.minus(symbolItem)
    }

    fun clearAll() {
        repository.update(selectedSymbols)
        selectedSymbols = emptyList()
    }

    fun selectSymbol(symbol: Symbol) {
        selectedSymbols =
            selectedSymbols.plus(SymbolItem(id = selectedSymbols.size, symbol = symbol))
        // provide next symbol suggestion
        viewModelScope.launch {
            getSuggestion(symbol)
        }
    }

    fun updateFavorite(symbol: Symbol, value: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(symbol, value)
        }
    }

    fun selectCategory(category: Category) {
        if (category != selectedCategory) {
            selectedCategory = category
            viewModelScope.launch {
                repository.getSymbolsByCategory(category).collect { symbols ->
                    _entries.postValue(listOf(category) + symbols)
                }
            }
        } else {
            selectedCategory = null
            viewModelScope.launch {
                getEntries()
            }
        }
    }

    private suspend fun getSuggestion(symbol: Symbol){
        repository.provideSuggestion(symbol).collect{symbols ->
            _entries.postValue(symbols)
        }
    }

    private suspend fun getEntries() {
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