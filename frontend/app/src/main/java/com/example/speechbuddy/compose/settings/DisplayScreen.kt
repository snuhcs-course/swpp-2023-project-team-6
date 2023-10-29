package com.example.speechbuddy.compose.settings

import SettingsTextUi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun DisplayScreen(
    modifier: Modifier,
    onBackClick: () -> Unit,
    darkModeChecked: Boolean,
    onDarkModeCheckedChange: (Boolean) -> Unit
) {
    val radioOptions =
        listOf(stringResource(id = R.string.tts_page), stringResource(id = R.string.symbol_page))
    var selectedItem by remember { mutableStateOf(radioOptions[0]) }

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier.padding(vertical = 30.dp, horizontal = 15.dp)
        ){
            BackButtonUi(onBackClick = onBackClick)
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ){
            SettingsTitleUi(
                modifier = modifier,
                title = stringResource(id = R.string.display_setting)
            )
            Spacer(modifier = modifier.height(15.dp))
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SettingsTextUi(modifier = modifier, text = stringResource(id = R.string.dark_mode))
                Spacer(modifier.weight(1f))
                Switch(
                    checked = darkModeChecked,
                    onCheckedChange = onDarkModeCheckedChange
                )
            }
            Spacer(modifier = modifier.height(15.dp))
            Row(
                modifier = modifier.fillMaxWidth()
            ) {
                SettingsTextUi(
                    modifier = modifier,
                    text = stringResource(id = R.string.initial_page)
                )
                Spacer(modifier.weight(1f))
                Column(modifier = modifier.selectableGroup()) {
                    radioOptions.forEach { label ->
                        Row(
                            modifier = modifier.selectable(
                                selected = (selectedItem == label),
                                onClick = { selectedItem = label },
                                role = Role.RadioButton
                            )
                        ) {
                            Text(text = label, style = MaterialTheme.typography.bodyMedium)
                            RadioButton(
                                selected = (selectedItem == label),
                                onClick = null
                            )
                            Spacer(modifier = modifier.padding(vertical = 15.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DisplayScreenPreview() {
    SpeechBuddyTheme {
        DisplayScreen(
            modifier = Modifier,
            onBackClick = {},
            darkModeChecked = true,
            onDarkModeCheckedChange = {})
    }
}