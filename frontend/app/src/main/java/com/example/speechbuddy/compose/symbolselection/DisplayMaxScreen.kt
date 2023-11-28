package com.example.speechbuddy.compose.symbolselection

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonLevel
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.utils.Constants
import com.example.speechbuddy.viewmodel.SymbolSelectionViewModel

@Composable
fun DisplayMaxScreen(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    showAppBars: () -> Unit,
    hideAppBars: () -> Unit,
    viewModel: SymbolSelectionViewModel = hiltViewModel()
) {
    val activity = LocalContext.current as Activity

    BackHandler(true) {
        viewModel.exitDisplayMax()
    }

    LaunchedEffect(true) {
        hideAppBars()
    }

    DisposableEffect(lifecycleOwner) {
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity.requestedOrientation = originalOrientation
            showAppBars()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(Float.MAX_VALUE),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(color = MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    LazyRow(
                        contentPadding = PaddingValues(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(viewModel.selectedSymbols) { symbolItem ->
                            DisplayMaxSymbolUi(
                                symbol = symbolItem.symbol
                            )
                        }
                    }
                }
            }

            ButtonUi(
                text = stringResource(id = R.string.exit),
                onClick = { viewModel.exitDisplayMax() },
                modifier = Modifier.widthIn(max = 312.dp),
                level = ButtonLevel.QUINARY
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DisplayMaxSymbolUi(
    symbol: Symbol
) {
    val symbolImagePath =
        if (symbol.id > Constants.DEFAULT_SYMBOL_COUNT)
            LocalContext.current.filesDir.toString().plus("/")  // needs to be modified
        else
            Constants.DEFAULT_SYMBOL_IMAGE_PATH

    Card(
        modifier = Modifier.size(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onBackground)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlideImage(
                    model = symbolImagePath.plus("symbol_${symbol.id}.png"),
                    contentDescription = symbol.text,
                    modifier = Modifier.height(104.dp),
                    contentScale = ContentScale.FillHeight
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(color = MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol.text,
                        textAlign = TextAlign.Center,
                        maxLines = Constants.MAXIMUM_LINES_FOR_SYMBOL_TEXT,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}