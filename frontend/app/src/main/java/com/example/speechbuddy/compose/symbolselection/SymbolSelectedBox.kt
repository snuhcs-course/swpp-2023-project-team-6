package com.example.speechbuddy.compose.symbolselection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.SymbolUi
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.ui.SpeechBuddyTheme


/**
 * Custom UI designed for SymbolSearchBox
 *
 * @param symbol data class Symbol to be passed to the UI
 * @param modifier the Modifier to be applied to this text field
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolSelectedBox(
    symbol: Symbol,
    modifier: Modifier = Modifier,
) {

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,

            ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(150.dp)
            ) {
                items(10) {
                    SymbolUi(
                        symbol = symbol,
                        onSelect = { /*TODO*/ }
                    ) {

                    }
                }

            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .background(Color.Yellow),

                ) {
                Button(
                    modifier = modifier
                        .fillMaxSize()
                        .background(Color.Red),
                    onClick = { /*TODO*/ }
                ) {

                }
            }

        }

    }

}


@Preview(showBackground = true)
@Composable
fun SymbolSelectedBoxPreview() {
    SpeechBuddyTheme {
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
        SymbolSelectedBox(
            symbol = previewSymbol,
            modifier = Modifier
        )
    }
}