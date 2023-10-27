package com.example.speechbuddy.compose.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun AlertDialogUi(
    modifier: Modifier,
    onConfirmButtonClick: () -> Unit,
    onDismissButtonClick: () -> Unit,
    title: String,
    content: String,
    confirmButtonString: String,
    dismissButtonString: String
){
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
                Text(confirmButtonString)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(dismissButtonString)
            }
        },
        containerColor = MaterialTheme.colorScheme.errorContainer
    )
}

@Preview
@Composable
fun AlertDialogUiPreview(){
    SpeechBuddyTheme {
        AlertDialogUi(
            modifier = Modifier,
            onConfirmButtonClick = { /*TODO*/ },
            onDismissButtonClick = { /*TODO*/ },
            title = "title",
            content = "content",
            confirmButtonString = "confirm",
            dismissButtonString = "dismiss"
        )
    }
}