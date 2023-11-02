package com.example.speechbuddy.compose.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.HomeTopAppBarUi
import com.example.speechbuddy.viewmodel.DisplaySettingsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySymbolSettings(
    modifier: Modifier, onBackClick: () -> Unit, viewModel: DisplaySettingsViewModel = hiltViewModel()
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(topBar = {
            HomeTopAppBarUi(
                title = stringResource(id = R.string.settings),
                onBackClick = onBackClick,
                isBackClickEnabled = true
            )
        }) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "My Symbol Settings")
            }
        }
    }
}