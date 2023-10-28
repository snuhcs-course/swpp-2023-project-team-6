package com.example.speechbuddy.compose.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.utils.Constants.Companion.MAXIMUM_LINES_FOR_SYMBOL_TEXT

/**
 * Custom UI designed for Symbol
 *
 * @param symbol data class Symbol to be passed to the UI
 * @param modifier the Modifier to be applied to this text field
 * @param onSelect called when this Symbol is clicked
 * @param onToggle called when the upper left start of this Symbol is clicked
 */
@ExperimentalMaterial3Api
@Composable
fun SymbolUi(
    symbol: Symbol,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit,
    onToggle: () -> Unit
) {
    OutlinedCard(
        onClick = onSelect,
        modifier = modifier.size(140.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer)
    ) {
        IconButton(
            onClick = onToggle,
            modifier = Modifier
                .padding(start = 4.dp, top = 4.dp)
                .size(16.dp)
        ) {
            Image(
                painter = when (symbol.isFavorite) {
                    true -> painterResource(id = R.drawable.fav_star_selected)
                    false -> painterResource(id = R.drawable.fav_star)
                },
                contentDescription = stringResource(id = R.string.favorite)
            )
        }

        OutlinedCard(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 100.dp, height = 80.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
        ) {
            Image(
                painter = painterResource(id = symbol.imageResId),
                contentDescription = symbol.text,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(80.dp)
            )
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol.text,
                textAlign = TextAlign.Center,
                maxLines = MAXIMUM_LINES_FOR_SYMBOL_TEXT,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@ExperimentalMaterial3Api
@Composable
fun SymbolPreview() {
    val previewSymbol = Symbol(
        id = 0,
        text = "앵무새",
        imageResId = R.drawable.top_app_bar_ic,
        category = Category(
            id = 0,
            text = "동물",
            imageResId = R.drawable.top_app_bar_ic
        ),
        isFavorite = true,
        isMine = false
    )

    SpeechBuddyTheme {
        SymbolUi(symbol = previewSymbol, onSelect = {}, onToggle = {})
    }
}