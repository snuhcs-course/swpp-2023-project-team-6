package com.example.speechbuddy.compose.settings

import SettingsTextUi
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.HomeTopAppBarUi
import com.example.speechbuddy.viewmodel.DisplayViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplaySettings(
    modifier: Modifier, onBackClick: () -> Unit, viewModel: DisplayViewModel = hiltViewModel()
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
            Column(
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
            ) {
                SettingsTitleUi(title = stringResource(id = R.string.display_setting))

                Spacer(modifier = modifier.height(15.dp))

                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SettingsTextUi(text = stringResource(id = R.string.dark_mode))

                    Spacer(modifier.weight(1f))

                    Switch(checked = viewModel.darkModeChecked,
                        onCheckedChange = { viewModel.onDarkModeCheckedChange() })
                }

                Spacer(modifier = modifier.height(15.dp))

                Row(
                    modifier = modifier.fillMaxWidth()
                ) {
                    SettingsTextUi(text = stringResource(id = R.string.initial_page))

                    Spacer(modifier.weight(1f))

                    Column {
                        Row(
                            modifier = modifier.height(30.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SettingsTextUi(text = stringResource(id = R.string.symbol_page))

                            RadioButton(selected = viewModel.selectedItem,
                                onClick = { viewModel.onSymbolClicked() })
                        }

                        Row(
                            modifier = modifier.height(30.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SettingsTextUi(text = stringResource(id = R.string.tts_page))

                            RadioButton(selected = !viewModel.selectedItem,
                                onClick = { viewModel.onTTSClicked() })
                        }
                    }
                }
            }
        }
    }
}