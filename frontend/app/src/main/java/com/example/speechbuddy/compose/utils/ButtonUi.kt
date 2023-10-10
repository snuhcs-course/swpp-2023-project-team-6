package com.example.speechbuddy.compose.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun ButtonUi(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: Boolean = false,
) {
    val containerColor: Color = when (error) {
        true -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor)
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

@Preview
@Composable
fun ButtonUiPreview() {
    SpeechBuddyTheme {
        ButtonUi(text = "커스텀 버튼", onClick = {})
    }
}