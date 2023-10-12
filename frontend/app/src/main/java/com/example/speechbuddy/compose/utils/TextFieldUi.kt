package com.example.speechbuddy.compose.utils

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.ui.SpeechBuddyTheme

/**
 * Custom UI designed for text fields.
 *
 * @param value the input text to be shown in the text field
 * @param onValueChange the callback that is triggered when the input service updates the text. An updated text comes as a parameter of the callback
 * @param modifier the Modifier to be applied to this text field
 * @param label the optional label to be displayed inside the text field container
 * @param supportingText the optional supporting text to be displayed below the text field
 * @param isError indicates if the text field's current value is in error. If set to true, the style of this text field by default will be displayed in error color
 * @param isValid indicates if the text field's current value is valid. If set to true, the style of this text field by default will be displayed in primary color
 * @param isHidden controls the visual transformation of the text field's current value. If set to true, the value of this text field will be displayed as a series of dots
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldUi(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    isValid: Boolean = false,
    isHidden: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp),
        textStyle = MaterialTheme.typography.bodyMedium,
        label = label,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = if (isHidden) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = getTextFieldColors(isValid)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun getTextFieldColors(isValid: Boolean): TextFieldColors {
    if (isValid) return TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colorScheme.onSurface,
        containerColor = MaterialTheme.colorScheme.surface,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.primary,
        focusedSupportingTextColor = MaterialTheme.colorScheme.primary,
        unfocusedSupportingTextColor = MaterialTheme.colorScheme.primary
    )
    else return TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colorScheme.onSurface,
        containerColor = MaterialTheme.colorScheme.surface,
        focusedBorderColor = MaterialTheme.colorScheme.tertiary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.tertiary,
        unfocusedLabelColor = MaterialTheme.colorScheme.outline,
        focusedSupportingTextColor = MaterialTheme.colorScheme.tertiary,
        unfocusedSupportingTextColor = MaterialTheme.colorScheme.outline
    )
}

@Preview(showBackground = true)
@Composable
fun TextFieldUiPreview() {
    SpeechBuddyTheme {
        TextFieldUi(
            value = "12345678",
            onValueChange = {},
            label = { Text("비밀번호") },
            supportingText = { Text("비밀번호가 일치하지 않습니다") },
            isError = true,
            isValid = false,
            isHidden = true,
        )
    }
}