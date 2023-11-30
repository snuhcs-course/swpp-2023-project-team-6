package com.example.speechbuddy.compose.symbolselection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.NoRippleInteractionSource
import com.example.speechbuddy.ui.models.DisplayMode

data class DisplayModeItem(
    val textResId: Int,
    val displayMode: DisplayMode,
    val containerColor: Color,
    val contentColor: Color
)

@Composable
fun DisplayModeMenu(
    currentDisplayMode: DisplayMode,
    onSelectDisplayMode: (DisplayMode) -> Unit
) {
    val displayModeItems = listOf(
        DisplayModeItem(
            R.string.all,
            DisplayMode.ALL,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        ),
        DisplayModeItem(
            R.string.symbol,
            DisplayMode.SYMBOL,
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        ),
        DisplayModeItem(
            R.string.category,
            DisplayMode.CATEGORY,
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        ),
        DisplayModeItem(
            R.string.favorite,
            DisplayMode.FAVORITE,
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
    )

    Row {
        displayModeItems.forEach { item ->
            val selected = item.displayMode == currentDisplayMode
            Card(
                modifier = Modifier.clickable(
                    interactionSource = NoRippleInteractionSource(),
                    indication = null,
                    onClick = { onSelectDisplayMode(item.displayMode) }),
                shape = RoundedCornerShape(
                    topStart = 10.dp, topEnd = 10.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor =
                    if (selected) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Card(
                    modifier = Modifier
                        .padding(8.dp),
                    shape = RoundedCornerShape(5.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                        if (selected) item.containerColor
                        else item.containerColor.copy(alpha = 0.5f),
                        contentColor =
                        if (selected) item.contentColor
                        else item.contentColor.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = stringResource(id = item.textResId),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}