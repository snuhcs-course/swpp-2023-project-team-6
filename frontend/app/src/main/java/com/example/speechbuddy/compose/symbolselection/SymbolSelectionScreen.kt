package com.example.speechbuddy.compose.symbolselection

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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.compose.utils.CategoryUi
import com.example.speechbuddy.compose.utils.SymbolUi
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.viewmodel.SymbolSelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolSelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: SymbolSelectionViewModel = hiltViewModel()
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 100.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            /* TODO: ViewModel 연결 */
            SymbolSearchTextField(
                value = viewModel.queryInput,
                onValueChange = { viewModel.setQuery(it) }
            )

            SelectedSymbolsBox(
                selectedSymbols = viewModel.selectedSymbols,
                onClear = { viewModel.clear(it) },
                onClearAll = { viewModel.clearAll() }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(16.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(140.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewModel.entries) { entry ->
                        when (entry) {
                            is Symbol -> SymbolUi(
                                symbol = entry,
                                onSelect = { viewModel.selectSymbol(entry) },
                                onFavoriteChange = { viewModel.toggleFavorite(entry, it) }
                            )

                            is Category -> CategoryUi(
                                category = entry,
                                onSelect = { viewModel.selectCategory(entry) }
                            )
                        }
                    }
                }
            }
        }
    }
}