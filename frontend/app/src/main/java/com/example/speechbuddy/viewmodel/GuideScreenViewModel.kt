package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GuideScreenViewModel @Inject internal constructor(
) : ViewModel() {
    // State to control the display of the GuideScreen or AlertDialog
    val showGuide = mutableStateOf(false)

    // Function to toggle the guide screen
    fun toggleGuide() {
        showGuide.value = !showGuide.value
    }
}
