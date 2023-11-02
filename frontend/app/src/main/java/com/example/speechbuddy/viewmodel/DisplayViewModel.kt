package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DisplayViewModel @Inject internal constructor() : ViewModel() {

    var darkModeChecked by mutableStateOf(false)
        private set

    var selectedItem by mutableStateOf(true)

    fun onDarkModeCheckedChange() {
        darkModeChecked = !darkModeChecked
    }

    fun onSymbolClicked() {
        selectedItem = true
    }

    fun onTTSClicked() {
        selectedItem = false
    }
}