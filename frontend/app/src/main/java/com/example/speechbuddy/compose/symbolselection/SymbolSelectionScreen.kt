package com.example.speechbuddy.compose.symbolselection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.compose.utils.CategoryUi
import com.example.speechbuddy.compose.utils.SymbolUi
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.viewmodel.SymbolSelectionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolSelectionScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    topAppBarState: MutableState<Boolean>,
    bottomNavBarState: MutableState<Boolean>,
    viewModel: SymbolSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val entries by viewModel.entries.observeAsState(initial = emptyList())

    // Used for automatic scroll
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .padding(start = 24.dp, top = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SymbolSearchTextField(
                value = viewModel.queryInput,
                onValueChange = { viewModel.setQuery(it) }
            )

            SelectedSymbolsBox(
                selectedSymbols = viewModel.selectedSymbols,
                lazyListState = lazyListState,
                onClear = { viewModel.clear(it) },
                onClearAll = { viewModel.clearAll() },
                onDisplayMax = { viewModel.enterDisplayMax() }
            )

            Column {
                DisplayModeMenu(
                    currentDisplayMode = uiState.displayMode,
                    onSelectDisplayMode = { viewModel.selectDisplayMode(it) }
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(topEnd = 20.dp)
                        ),
                    state = lazyGridState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    /**
                     * Without the elvis operator, null pointer exception arises.
                     * Do NOT erase the elvis operator although it seems useless!
                     */
                    items(entries) { entry ->
                        when (entry) {
                            is Symbol -> SymbolUi(
                                symbol = entry,
                                onSelect = {
                                    coroutineScope.launch {
                                        lazyGridState.animateScrollToItem(0)
                                    }
                                },
                                onFavoriteChange = { viewModel.updateFavorite(entry, it) }
                            )

                            is Category -> CategoryUi(
                                category = entry,
                                onSelect = {
                                    coroutineScope.launch {
                                        viewModel.selectCategory(entry)
                                        lazyGridState.animateScrollToItem(0)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.isDisplayMax) {
        DisplayMaxScreen(
            showAppBars = {
                topAppBarState.value = true
                bottomNavBarState.value = true
            },
            hideAppBars = {
                topAppBarState.value = false
                bottomNavBarState.value = false
            }
        )
    }
}