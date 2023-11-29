package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.symbolselection.SymbolSearchTextField
import com.example.speechbuddy.compose.utils.NoRippleInteractionSource
import com.example.speechbuddy.compose.utils.SymbolUi
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.ui.models.MySymbolSettingsDisplayMode
import com.example.speechbuddy.utils.Constants.Companion.DEFAULT_SYMBOL_COUNT
import com.example.speechbuddy.utils.Constants.Companion.DEFAULT_SYMBOL_IMAGE_PATH
import com.example.speechbuddy.utils.Constants.Companion.MAXIMUM_LINES_FOR_SYMBOL_TEXT
import com.example.speechbuddy.viewmodel.MySymbolSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySymbolSettings(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    viewModel: MySymbolSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val symbols by viewModel.symbols.observeAsState(initial = emptyList())

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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SymbolSearchTextField(
                value = viewModel.queryInput,
                onValueChange = { viewModel.setQuery(it) }
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                DisplayModeMenu(
                    currentDisplayMode = uiState.mySymbolSettingsDisplayMode,
                    onSelectDisplayMode = { viewModel.selectDisplayMode(it) }
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(
                                topEnd = 20.dp,
                                bottomStart = 20.dp,
                                bottomEnd = 20.dp
                            )
                        ),
                    state = lazyGridState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.mySymbolSettingsDisplayMode == MySymbolSettingsDisplayMode.SYMBOL) {
                        items(symbols ?: emptyList()) { symbol ->
                            MySymbolUi(
                                symbol = symbol,
                                viewModel = viewModel
                            )
                        }
                    } else {
                        items(symbols ?: emptyList()) { symbol ->
                            SymbolUi(
                                symbol = symbol,
                                onSelect = {},
                                onFavoriteChange = { viewModel.updateFavorite(symbol, it) }
                            )
                        }
                    }
                }
            }
            if (uiState.mySymbolSettingsDisplayMode == MySymbolSettingsDisplayMode.SYMBOL) {
                DeleteButtonUi(
                    text = stringResource(id = R.string.delete_my_symbol),
                    onClick = { viewModel.deleteCheckedSymbols() },
                    isEnabled = viewModel.checkedSymbols.isNotEmpty()
                )
            }
        }
    }
}

data class DisplayModeItem(
    val textResId: Int,
    val displayMode: MySymbolSettingsDisplayMode,
    val containerColor: Color,
    val contentColor: Color
)

@Composable
fun DisplayModeMenu(
    currentDisplayMode: MySymbolSettingsDisplayMode,
    onSelectDisplayMode: (MySymbolSettingsDisplayMode) -> Unit
) {
    val displayModeItems = listOf(
        DisplayModeItem(
            R.string.my_symbol,
            MySymbolSettingsDisplayMode.SYMBOL,
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        ),
        DisplayModeItem(
            R.string.favorite,
            MySymbolSettingsDisplayMode.FAVORITE,
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

@OptIn(ExperimentalGlideComposeApi::class)
@ExperimentalMaterial3Api
@Composable
fun MySymbolUi(
    symbol: Symbol,
    modifier: Modifier = Modifier,
    viewModel: MySymbolSettingsViewModel
) {
    val filepath =
        if (symbol.id > DEFAULT_SYMBOL_COUNT)
            LocalContext.current.filesDir.toString().plus("/")
        else
            DEFAULT_SYMBOL_IMAGE_PATH

    val isChecked = viewModel.checkedSymbols.contains(symbol)

    Card(
        onClick = { viewModel.toggleSymbolChecked(symbol) },
        modifier = modifier.size(140.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .size(16.dp)
                    .border(1.dp, Color.Black)
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { viewModel.toggleSymbolChecked(symbol) },
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f),
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.White,
                        uncheckedColor = Color.White,
                        checkmarkColor = Color.Black
                    )
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlideImage(
                    model = filepath.plus("symbol_${symbol.id}.png"),
                    contentDescription = symbol.text,
                    modifier = Modifier.height(90.dp),
                    contentScale = ContentScale.FillHeight
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(color = MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol.text,
                        textAlign = TextAlign.Center,
                        maxLines = MAXIMUM_LINES_FOR_SYMBOL_TEXT,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteButtonUi(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = isEnabled,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}