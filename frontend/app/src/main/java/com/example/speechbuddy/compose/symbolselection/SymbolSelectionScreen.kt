package com.example.speechbuddy.compose.symbolselection

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.SymbolUi
import com.example.speechbuddy.compose.utils.TopAppBarUi
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.ui.SpeechBuddyTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolSelectionScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    /* TODO: ViewModel 연결 후 삭제 */
    val previewSymbol = Symbol(
        id = 0,
        text = "119에 전화해주세요",
        imageResId = R.drawable.symbol_0,
        categoryId = 0,
        isFavorite = true,
        isMine = false
    )
    val selectedSymbols = List(size = 10, init = { previewSymbol })

    Surface(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = { TopAppBarUi(onBackClick = onBackClick) }
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize()
                    .padding(top = 60.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                /* TODO: ViewModel 연결 */
                SymbolSearchTextField(value = "검색어", onValueChange = {})

                SelectedSymbolsBox(selectedSymbols = selectedSymbols, onClearAll = {})

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(140.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(selectedSymbols) { symbol ->
                            SymbolUi(symbol = symbol, onSelect = {}, onFavoriteChange = {})
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SymbolSelectionScreenPreview() {
    SpeechBuddyTheme {
        SymbolSelectionScreen(onBackClick = {})
    }
}