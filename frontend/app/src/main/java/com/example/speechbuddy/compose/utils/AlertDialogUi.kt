package com.example.speechbuddy.compose.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogUi(
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    androidx.compose.material3.AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("확인")
            }
        }
    )
}