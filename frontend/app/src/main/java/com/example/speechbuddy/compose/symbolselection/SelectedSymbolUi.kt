package com.example.speechbuddy.compose.symbolselection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.utils.Constants


/**
 * Custom UI designed for SymbolSearchBox
 *
 * @param symbol data class Symbol to be passed to the UI
 * @param modifier the Modifier to be applied to this text field
 * @param onDelete called when the upper left icon of this Symbol is clicked
 */
@ExperimentalMaterial3Api
@Composable
fun SelectedSymbolUi(
    symbol: Symbol,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit
) {
    Card(
        modifier = modifier.size(100.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onBackground)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(22.dp)
                    .padding(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(id = R.string.symbol_unselect)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = symbol.imageResId),
                    contentDescription = symbol.text,
                    modifier = Modifier.height(65.dp),
                    contentScale = ContentScale.FillHeight
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp)
                        .background(color = MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol.text,
                        textAlign = TextAlign.Center,
                        maxLines = Constants.MAXIMUM_LINES_FOR_SYMBOL_TEXT,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@ExperimentalMaterial3Api
@Composable
fun SelectedSymbolUiPreview() {
    val previewSymbol = Symbol(
        id = 0,
        text = "119에 전화해주세요",
        imageResId = R.drawable.symbol_0,
        categoryId = 0,
        isFavorite = true,
        isMine = false
    )

    SpeechBuddyTheme {
        SelectedSymbolUi(symbol = previewSymbol, onDelete = {})
    }
}