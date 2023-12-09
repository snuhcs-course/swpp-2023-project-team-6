package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.ui.models.InitialPage
import com.example.speechbuddy.viewmodel.DisplaySettingsViewModel

@Composable
fun DisplaySettings(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    viewModel: DisplaySettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TitleUi(title = stringResource(id = R.string.display_settings))

            Spacer(modifier = modifier.height(20.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SettingsRow(
                    label = stringResource(id = R.string.dark_mode),
                    content = {
                        Switch(
                            checked = uiState.isDarkModeEnabled,
                            onCheckedChange = { viewModel.setDarkMode(it) },
                            modifier = Modifier.heightIn(max = 32.dp).testTag("dark_mode")
                        )
                    }
                )

                SettingsRow(
                    label = stringResource(id = R.string.initial_page),
                    content = {
                        InitialPageColumn(
                            initialPage = uiState.initialPage,
                            onSelectInitialPage = {
                                viewModel.setInitialPage(it)
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun InitialPageColumn(
    initialPage: InitialPage,
    onSelectInitialPage: (InitialPage) -> Unit
) {
    Column(
        modifier = Modifier.width(140.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SettingsRow(
            label = stringResource(id = R.string.talk_with_symbols),
            content = {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    RadioButton(
                        selected = initialPage == InitialPage.SYMBOL_SELECTION,
                        onClick = { onSelectInitialPage(InitialPage.SYMBOL_SELECTION) },
                        modifier = Modifier.size(20.dp).testTag("initial_page_symbol")
                    )
                }
            }
        )

        SettingsRow(
            label = stringResource(id = R.string.talk_with_speech),
            content = {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    RadioButton(
                        selected = initialPage == InitialPage.TEXT_TO_SPEECH,
                        onClick = { onSelectInitialPage(InitialPage.TEXT_TO_SPEECH) },
                        modifier = Modifier.size(20.dp).testTag("initial_page_tts")
                    )
                }
            }
        )
    }
}