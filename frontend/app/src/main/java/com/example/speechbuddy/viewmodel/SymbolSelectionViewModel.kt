package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.repository.SymbolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SymbolSelectionViewModel @Inject internal constructor(
    repository: SymbolRepository
) : ViewModel() {

    var queryInput by mutableStateOf("")
        private set

    var selectedSymbols by mutableStateOf(listOf<Symbol>())
        private set

    val entries: LiveData<List<Symbol>> = repository.getSymbols().asLiveData()

    fun setQuery(input: String) {
        queryInput = input
    }

    fun clear(symbol: Symbol) {
        val mutableSelectedSymbols = selectedSymbols.toMutableList()
        mutableSelectedSymbols.remove(symbol)
        selectedSymbols = mutableSelectedSymbols.toList()
    }

    fun clearAll() {
        selectedSymbols = listOf()
    }

    fun selectSymbol(symbol: Symbol) {
        selectedSymbols = selectedSymbols.plus(symbol)
    }

    fun toggleFavorite(symbol: Symbol, value: Boolean) {
        /* TODO */
    }

    fun selectCategory(category: Category) {
        /* TODO */
    }
}