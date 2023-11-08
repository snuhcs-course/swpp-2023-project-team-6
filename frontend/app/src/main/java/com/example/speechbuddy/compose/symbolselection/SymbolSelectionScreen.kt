package com.example.speechbuddy.compose.symbolselection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.CategoryUi
import com.example.speechbuddy.compose.utils.HomeTopAppBarUi
import com.example.speechbuddy.compose.utils.SymbolUi
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.ui.models.DisplayMode
import com.example.speechbuddy.ui.models.SymbolSelectionUiState
import com.example.speechbuddy.viewmodel.SymbolSelectionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolSelectionScreen(
    modifier: Modifier = Modifier,
    bottomPaddingValues: PaddingValues,
    viewModel: SymbolSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val entries by viewModel.entries.observeAsState(initial = emptyList())

    // Used for automatic scroll
    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                HomeTopAppBarUi(title = stringResource(id = R.string.talk_with_symbols), actions = {
                    IconButton(onClick = { viewModel.expandMenu() }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(id = R.string.menu)
                        )
                    }
                    DropdownMenuUi(
                        uiState = uiState,
                        onDismissRequest = { viewModel.dismissMenu() },
                        onSelectDisplayMode = {
                            coroutineScope.launch {
                                viewModel.selectDisplayMode(it)
                                lazyGridState.scrollToItem(0)
                            }
                        }
                    )
                })
            }
        ) { topPaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = topPaddingValues.calculateTopPadding(),
                        bottom = bottomPaddingValues.calculateBottomPadding()
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
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(140.dp),
                        state = lazyGridState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        /**
                         * Without the elvis operator, null pointer exception arises.
                         * Do NOT erase the seemingly useless elvis operator!
                         */
                        items(entries ?: emptyList()) { entry ->
                            when (entry) {
                                is Symbol -> SymbolUi(
                                    symbol = entry,
                                    onSelect = { viewModel.selectSymbol(entry) },
                                    onFavoriteChange = { viewModel.updateFavorite(entry, it) }
                                )

                                is Category -> CategoryUi(
                                    category = entry,
                                    onSelect = {
                                        coroutineScope.launch {
                                            viewModel.selectCategory(entry)
                                            lazyGridState.scrollToItem(0)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class DisplayModeItem(
    val textResId: Int,
    val displayMode: DisplayMode
)

@Composable
private fun DropdownMenuUi(
    uiState: SymbolSelectionUiState,
    onDismissRequest: () -> Unit,
    onSelectDisplayMode: (DisplayMode) -> Unit
) {
    val displayModeItems = listOf(
        DisplayModeItem(R.string.display_all, DisplayMode.ALL),
        DisplayModeItem(R.string.display_symbols, DisplayMode.SYMBOL),
        DisplayModeItem(R.string.display_categories, DisplayMode.CATEGORY),
        DisplayModeItem(R.string.display_favorites, DisplayMode.FAVORITE)
    )
    DropdownMenu(
        expanded = uiState.isMenuExpanded,
        onDismissRequest = onDismissRequest
    ) {
        displayModeItems.forEach { item ->
            val selected = item.displayMode == uiState.displayMode
            DropdownMenuItem(
                text = { Text(text = stringResource(id = item.textResId)) },
                onClick = { onSelectDisplayMode(item.displayMode) },
                trailingIcon = {
                    if (selected)
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(id = R.string.currently_selected_menu)
                        )
                }
            )
        }
    }
}