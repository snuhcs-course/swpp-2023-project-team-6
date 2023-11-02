package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Entry
import com.example.speechbuddy.domain.models.Symbol
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SymbolSelectionViewModel @Inject internal constructor() : ViewModel() {

    var queryInput by mutableStateOf("")
        private set

    var selectedSymbols by mutableStateOf(listOf<Symbol>())
        private set

    var entries by mutableStateOf(listOf<Entry>())
        private set

    /* TODO: 나중에 삭제 */
    private val initialEntries = listOf(
        Symbol(
            id = 0,
            text = "119에 전화해주세요",
            imageResId = R.drawable.symbol_1,
            categoryId = 0,
            isFavorite = true,
            isMine = false
        ),
        Symbol(
            id = 0,
            text = "119에 전화해주세요",
            imageResId = R.drawable.symbol_1,
            categoryId = 0,
            isFavorite = true,
            isMine = false
        )
    )

    private val secondaryEntries = listOf(
        Symbol(
            id = 0,
            text = "119에 전화해주세요",
            imageResId = R.drawable.symbol_1,
            categoryId = 0,
            isFavorite = true,
            isMine = false
        ),
        Symbol(
            id = 0,
            text = "우와",
            imageResId = R.drawable.symbol_1,
            categoryId = 0,
            isFavorite = true,
            isMine = false
        )
    )

    init {
        entries = initialEntries
    }

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
        entries = initialEntries
    }

    fun selectSymbol(symbol: Symbol) {
        selectedSymbols = selectedSymbols.plus(symbol)
        entries = secondaryEntries
    }

    fun toggleFavorite(symbol: Symbol, value: Boolean) {
        /* TODO */
    }

    fun selectCategory(category: Category) {
        /* TODO */
    }
}