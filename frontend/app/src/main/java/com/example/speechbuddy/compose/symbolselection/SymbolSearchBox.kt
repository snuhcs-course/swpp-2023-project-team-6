package com.example.speechbuddy.compose.symbolselection

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.ui.SpeechBuddyTheme

/**
 * Custom UI designed for SymbolSearchBox
 *
 * @param value the input text to be shown in the text field
 * @param onValueChange the callback that is triggered when the input service updates the text. An updated text comes as a parameter of the callback
 * @param modifier the Modifier to be applied to this text field
 * @param isEnabled indicates if the text in the text field is modifiable
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolSearchBox(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp),
        enabled = isEnabled,
        textStyle = MaterialTheme.typography.bodyMedium,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Find corresponding words"
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary
        )
    )
}


@Preview(showBackground = true)
@Composable
fun SymbolSearchBoxPreview() {
    SpeechBuddyTheme {
        SymbolSearchBox(
            value = "검색어를 입력하세요",
            onValueChange = {},
        )
    }
}