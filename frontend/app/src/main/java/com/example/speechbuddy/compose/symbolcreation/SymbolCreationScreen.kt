package com.example.speechbuddy.compose.symbolcreation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.HomeTopAppBarUi
import com.example.speechbuddy.compose.utils.TextFieldUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.ui.SpeechBuddyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolCreationScreen(
    modifier: Modifier = Modifier,
    bottomPaddingValues: PaddingValues
) {
    val myItemsList = listOf(
        "Option 1", "Option 2", "Option 3", "Option 4", "Option 5", "Option 6",
        "Option 7", "Option 8", "Option 9", "Option 10", "Option 11", "Option 12",
        "Option 13", "Option 14", "Option 15", "Option 16", "Option 17", "Option 18",
        "Option 19", "Option 20", "Option 21", "Option 22", "Option 23", "Option 24"
    )

    // State to hold the currently selected value
    var selectedValue by remember { mutableStateOf("") }
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                HomeTopAppBarUi(title = stringResource(id = R.string.symbol_creation))
            }
        ) { topPaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = topPaddingValues.calculateTopPadding(),
                        bottom = bottomPaddingValues.calculateBottomPadding()
                    )
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleUi(
                    title = stringResource(id = R.string.create_symbol_text),
                    description = stringResource(id = R.string.create_symbol_explain)
                )

                Spacer(modifier = Modifier.height(30.dp))

                AddPhotoButton(onClick = {})

                Spacer(modifier = Modifier.height(30.dp))

                TextFieldUi(
                    value = "blablabla",
                    onValueChange = {},
                    label = { Text(stringResource(R.string.new_symbol_name)) },
                    isError = false,
                    isValid = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                DropdownUi(
                    selectedValue = selectedValue,
                    onValueChange = { newValue -> selectedValue = newValue },
                    items = myItemsList,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.category)) },
                    isError = false
                )

                ButtonUi(
                    text = stringResource(id = R.string.register),
                    onClick = {},
                    modifier = Modifier.offset(y = 50.dp),
                    isEnabled = true,
                    isError = false
                )
            }
        }
    }
}

@Composable
private fun DropdownUi(
    selectedValue: String,
    onValueChange: (String) -> Unit,
    items: List<String>,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(10.dp)
            )
            .clickable { expanded = true }
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (selectedValue.isNotEmpty()) {
                Text(
                    text = selectedValue,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                label?.invoke() ?: Text(
                    "Select an option",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = stringResource(R.string.dropdown_icon_description)
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .width(300.dp)
            .heightIn(max = 200.dp),
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = item,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = {
                    onValueChange(item)
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun AddPhotoButton(
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(120.dp)
            .clickable { onClick() }
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.outline_add_a_photo_24),
            contentDescription = stringResource(R.string.symbol_creation)
        )
    }
}

@Preview
@Composable
private fun SymbolCreationScreenPreview() {
    SpeechBuddyTheme {
        SymbolCreationScreen(
            modifier = Modifier,
            bottomPaddingValues = PaddingValues(16.dp)
        )
    }
}