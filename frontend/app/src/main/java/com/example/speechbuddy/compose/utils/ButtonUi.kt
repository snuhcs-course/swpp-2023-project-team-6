package com.example.speechbuddy.compose.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.ui.SpeechBuddyTheme

enum class ButtonLevel {
    PRIMARY, SECONDARY
}

/**
 * Custom UI designed for buttons.
 *
 * @param text displayed on the center of this button
 * @param onClick called when this button is clicked
 * @param modifier the Modifier to be applied to this button
 * @param isEnabled controls the enabled state of this button. When false, this component will not respond to user input, and it will appear visually disabled and disabled to accessibility services
 * @param isError defines the color and the enabled state of this button. When true, this component will be disabled, and it will be displayed in error color
 * @param level should be either ButtonLevel.PRIMARY or ButtonLevel.SECONDARY. ButtonLevel.PRIMARY is the default value
 */
@Composable
fun ButtonUi(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isError: Boolean = false,
    level: ButtonLevel = ButtonLevel.PRIMARY
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = isEnabled,
        shape = RoundedCornerShape(10.dp),
        colors = getButtonColors(level, isError)
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun getButtonColors(level: ButtonLevel, isError: Boolean): ButtonColors {
    val buttonColors: ButtonColors = when {
        level == ButtonLevel.PRIMARY && isError -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
            disabledContainerColor = MaterialTheme.colorScheme.error,
            disabledContentColor = MaterialTheme.colorScheme.onError
        )

        level == ButtonLevel.PRIMARY && !isError -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )

        level == ButtonLevel.SECONDARY && isError -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
            disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
        )

        level == ButtonLevel.SECONDARY && !isError -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

        else -> ButtonDefaults.buttonColors()
    }
    return buttonColors
}

@Preview
@Composable
fun ButtonUiPreview() {
    SpeechBuddyTheme {
        ButtonUi(text = "커스텀 버튼", onClick = {}, isError = true, level = ButtonLevel.SECONDARY)
    }
}