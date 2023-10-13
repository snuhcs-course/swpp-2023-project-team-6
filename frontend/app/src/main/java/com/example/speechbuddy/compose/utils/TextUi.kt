package com.example.speechbuddy.compose.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun TextUi(
    textId: Int,
    isError: Boolean = false,
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        text = stringResource(id = textId),
        style = MaterialTheme.typography.bodySmall,
        color = getTextColor(isError = isError),
    )
}

@Composable
private fun getTextColor(
    isError: Boolean
): Color {
    return if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onBackground
    }
}