package com.example.speechbuddy.compose.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun AlertDialogUi(
    title: String,
    text: String,
    dismissButtonText: String,
    confirmButtonText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                )
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(dismissButtonText)
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    )
}

@Preview
@Composable
fun AlertDialogUiPreview() {
    SpeechBuddyTheme {
        AlertDialogUi(
            title = "title",
            text = "text",
            dismissButtonText = "dismiss",
            confirmButtonText = "confirm",
            onDismiss = {},
            onConfirm = {}
        )
    }
}