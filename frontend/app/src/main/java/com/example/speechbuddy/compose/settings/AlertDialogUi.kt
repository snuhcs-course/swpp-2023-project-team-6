package com.example.speechbuddy.compose.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun AlertDialogUi(
    onConfirmButtonClick: () -> Unit,
    onDismissButtonClick: () -> Unit,
    title: String,
    content: String,
    confirmButtonText: String,
    dismissButtonText: String
) {
    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            Text(text = content)
        },
        onDismissRequest = {
            onDismissButtonClick
        },
        confirmButton = {
            Button(
                onClick = onConfirmButtonClick,
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
                onClick = onDismissButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(dismissButtonText)
            }
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    )
}

@Preview
@Composable
fun AlertDialogUiPreview() {
    SpeechBuddyTheme {
        AlertDialogUi(
            onConfirmButtonClick = { /*TODO*/ },
            onDismissButtonClick = { /*TODO*/ },
            title = "title",
            content = "content",
            confirmButtonText = "confirm",
            dismissButtonText = "dismiss"
        )
    }
}