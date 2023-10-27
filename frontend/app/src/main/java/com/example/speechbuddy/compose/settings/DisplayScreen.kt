package com.example.speechbuddy.compose.settings

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun DisplayScreen(
    modifier: Modifier,
    onBackClick: () -> Unit,
    onBackupClick: () -> Unit,
    lastBackupDate: String,
    darkModeChecked: Boolean,
    onDarkModeCheckedChange: (Boolean) -> Unit
){
    val radioOptions = listOf(stringResource(id = R.string.tts_page), stringResource(id = R.string.symbol_page))
    var selectedItem by remember{mutableStateOf(radioOptions[0])}
    
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column (
            modifier = modifier.padding(start = 30.dp, end = 30.dp, top = 10.dp)
        ){
            BackButtonUi(onBackClick = onBackClick)
            Spacer(modifier = modifier.height(136.dp))
            SettingsTitleUi(modifier = modifier, title = stringResource(id = R.string.display_setting))
            Spacer(modifier = modifier.height(15.dp))
            Row (
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                SettingsTextUi(modifier = modifier, text = stringResource(id = R.string.dark_mode))
                Spacer(modifier.weight(1f))
                Switch(
                    checked = darkModeChecked,
                    onCheckedChange = onDarkModeCheckedChange
                )
            }
            Spacer(modifier = modifier.height(15.dp))
            Row (
                modifier = modifier.fillMaxWidth()
            ){
                SettingsTextUi(modifier = modifier, text = stringResource(id = R.string.initial_page))
                Spacer(modifier.weight(1f))
                Column(modifier = modifier.selectableGroup()){
                    radioOptions.forEach{ label ->
                        Row(
                            modifier = modifier.selectable(
                                selected = (selectedItem == label),
                                onClick = {selectedItem = label},
                                role = Role.RadioButton
                            )
                        ){
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
            Spacer(modifier = modifier.height(165.dp))
            ButtonUi(modifier = modifier, text = stringResource(id = R.string.backup_now), onClick = onBackupClick)
        }
    }
}

@Preview
@Composable
fun DisplayScreenPreview(){
    SpeechBuddyTheme {
        DisplayScreen(modifier = Modifier, onBackClick = {}, onBackupClick = {}, lastBackupDate = "2023.10.27", darkModeChecked = true, onDarkModeCheckedChange = {})
    }
}