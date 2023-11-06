package com.example.speechbuddy.compose.symbolselection

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.viewmodel.SymbolItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedSymbolsBox(
    selectedSymbols: List<SymbolItem>,
    onClear: (SymbolItem) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(color = MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            LazyRow(
                contentPadding = PaddingValues(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(selectedSymbols) { symbolItem ->
                    SelectedSymbolUi(
                        symbol = symbolItem.symbol,
                        onClear = { onClear(symbolItem) }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .width(50.dp)
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Button(
                onClick = onClearAll,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                enabled = selectedSymbols.isNotEmpty(),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                contentPadding = PaddingValues(2.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.clear_all),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
fun SelectedSymbolsBoxPreview() {
    val previewSymbol = Symbol(
        id = 1,
        text = "119에 전화해주세요",
        imageUrl = null,
        categoryId = 1,
        isFavorite = true,
        isMine = false
    )
    val previewSymbolItem = SymbolItem(
        id = 1,
        symbol = previewSymbol
    )
    val selectedSymbols = List(size = 10, init = { previewSymbolItem })

    SpeechBuddyTheme {
        SelectedSymbolsBox(selectedSymbols = selectedSymbols, onClear = {}, onClearAll = {})
    }
}