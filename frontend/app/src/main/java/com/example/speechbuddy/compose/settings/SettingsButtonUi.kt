package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.speechbuddy.ui.SpeechBuddyTheme

/**
 * Custom UI designed for buttons.
 *
 * @param text displayed on the center of this button
 * @param onClick called when this button is clicked
 * @param modifier the Modifier to be applied to this button
 * @param isEnabled controls the enabled state of this button. When false, this component will not respond to user input, and it will appear visually disabled and disabled to accessibility services
 * @param isError defines the color and the enabled state of this button. When true, this component will be disabled, and it will be displayed in error color
 * @param level should be ButtonLevel.PRIMARY, ButtonLevel.SECONDARY, or ButtonLevel.TERTIARY. ButtonLevel.PRIMARY is the default value
 */
@Composable
fun SettingsButtonUi(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,

    ) {
    TextButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview
@Composable
fun SettingsButtonUiPreview() {
    SpeechBuddyTheme {
        SettingsButtonUi(
            text = "text",
            onClick = {},
            modifier = Modifier
        )
    }
}