package com.example.speechbuddy.viewmodel;

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.repository.SymbolRepository;
import com.example.speechbuddy.repository.WeightTableRepository
import com.example.speechbuddy.ui.models.MySymbolSettingsDisplayMode
import com.example.speechbuddy.ui.models.MySymbolSettingsUiState

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MySymbolSettingsViewModel @Inject internal constructor(
    private val weightTableRepository: WeightTableRepository,
    private val repository:SymbolRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow(MySymbolSettingsUiState())
    val uiState: StateFlow<MySymbolSettingsUiState> = _uiState.asStateFlow()

    private val _symbols = MutableLiveData<List<Symbol>>()
    val symbols: LiveData<List<Symbol>> get() = _symbols

    private val _checkedSymbols = mutableStateListOf<Symbol>()
    val checkedSymbols: List<Symbol> get() = _checkedSymbols

    var queryInput by mutableStateOf("")
        private set

    private var getSymbolsJob: Job? = null

    init {
        getSymbols()
    }

    fun setQuery(input: String) {
        queryInput = input
        getSymbols(input)
    }

    fun toggleSymbolChecked(symbol: Symbol) {
        if (_checkedSymbols.contains(symbol)) {
            _checkedSymbols.remove(symbol)
        } else {
            _checkedSymbols.add(symbol)
        }
    }

    fun updateFavorite(symbol: Symbol, value: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(symbol, value)
        }
    }

    fun selectDisplayMode(displayMode: MySymbolSettingsDisplayMode) {
        _uiState.update { currentState ->
            currentState.copy(
                mySymbolSettingsDisplayMode = displayMode
            )
        }
        getSymbols()
    }

    fun deleteCheckedSymbols() {
        viewModelScope.launch {
            val checkedSymbols = _checkedSymbols.toList()
            for (symbol in checkedSymbols) {
                repository.deleteSymbol(symbol)
                weightTableRepository.updateWeightTableForDeletedSymbol(symbol)
            }
            _checkedSymbols.clear()
        }
    }

    private fun getSymbols(query: String? = null) {
        getSymbolsJob?.cancel()
        getSymbolsJob = viewModelScope.launch {
            when (_uiState.value.mySymbolSettingsDisplayMode) {
                MySymbolSettingsDisplayMode.SYMBOL -> {
                    if(query!=null){
                        repository.getUserSymbols(query).collect{ symbols ->
                            _symbols.postValue(symbols)
                        }
                    }
                    else {
                        repository.getUserSymbols(queryInput).collect{ symbols ->
                            _symbols.postValue(symbols)
                        }
                    }
                }

                MySymbolSettingsDisplayMode.FAVORITE -> {
                    repository.getFavoriteSymbols(queryInput).collect { symbols ->
                        _symbols.postValue(symbols)
                    }
                }
            }
        }
    }

}
