package com.example.speechbuddy.compose.utils

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.ui.SpeechBuddyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldUi(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp),
        textStyle = MaterialTheme.typography.bodySmall,
        label = label,
        placeholder = placeholder,
        isError = isError,
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
    )
}

@Preview(showBackground = true)
@Composable
fun TextFieldUiPreview() {
    SpeechBuddyTheme {
        TextFieldUi(value = "사용자 인풋", onValueChange = {}, label = { Text("라벨") })
    }
}