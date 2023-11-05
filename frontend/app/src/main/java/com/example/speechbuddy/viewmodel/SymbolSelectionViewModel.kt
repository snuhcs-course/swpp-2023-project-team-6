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

    var selectedSymbols by mutableStateOf(listOf<Symbol>())
        private set

    private var selectedCategory by mutableStateOf<Category?>(null)

    private val _entries = MutableLiveData<List<Entry>>()
    val entries: LiveData<List<Entry>> get() = _entries

    init {
        viewModelScope.launch {
            repository.getCategories().collect { categories ->
                _entries.postValue(categories)
            }
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
                isMenuExpanded = false,
                displayMode = displayMode
            )
        }
    }

    fun setQuery(input: String) {
        queryInput = input
    }

    fun clear(symbol: Symbol) {
        selectedSymbols = selectedSymbols - symbol
    }

    fun clearAll() {
        selectedSymbols = emptyList()
    }

    fun selectSymbol(symbol: Symbol) {
        selectedSymbols = selectedSymbols.plus(symbol)
    }

    fun toggleFavorite(symbol: Symbol, value: Boolean) {
        /* TODO */
    }

    fun selectCategory(category: Category) {
        if (category != selectedCategory) {
            selectedCategory = category
            viewModelScope.launch {
                repository.getSymbolsFromCategory(category).collect { symbols ->
                    _entries.postValue(listOf(category) + symbols)
                }
            }
        } else {
            selectedCategory = null
            viewModelScope.launch {
                repository.getCategories().collect { categories ->
                    _entries.postValue(categories)
                }
            }
        }
    }

}