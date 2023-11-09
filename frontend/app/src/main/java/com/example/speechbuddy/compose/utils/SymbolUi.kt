package com.example.speechbuddy.compose.utils

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.speechbuddy.R
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.utils.Constants.Companion.DEFAULT_SYMBOL_IMAGE_PATH
import com.example.speechbuddy.utils.Constants.Companion.MAXIMUM_LINES_FOR_SYMBOL_TEXT

/**
 * Custom UI designed for Symbol
 *
 * @param symbol data class Symbol to be passed to the UI
 * @param modifier the Modifier to be applied to this outlined card
 * @param onSelect called when this Symbol is clicked
 * @param onFavoriteChange called when the upper left icon of this Symbol is clicked
 */
@OptIn(ExperimentalGlideComposeApi::class)
@ExperimentalMaterial3Api
@Composable
fun SymbolUi(
    symbol: Symbol,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit,
    onFavoriteChange: (Boolean) -> Unit
) {
    val filepath = if(symbol.id>500){
        LocalContext.current.filesDir.toString().plus("/")  // needs to be modified
    } else{
        DEFAULT_SYMBOL_IMAGE_PATH
    }

    Card(
        onClick = onSelect,
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            IconToggleButton(
                checked = symbol.isFavorite,
                onCheckedChange = onFavoriteChange,
                modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp)
                    .zIndex(1f),
                colors = IconButtonDefaults.iconToggleButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    checkedContentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = when (symbol.isFavorite) {
                        true -> Icons.Default.Favorite
                        false -> Icons.Default.FavoriteBorder
                    },
                    contentDescription = stringResource(id = R.string.favorite)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlideImage(
                    model = filepath.plus("symbol_${symbol.id}.png"),
                    contentDescription = symbol.text,
                    modifier = Modifier.height(95.dp),
                    contentScale = ContentScale.FillHeight
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(color = MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 10.dp),
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
    }
}
