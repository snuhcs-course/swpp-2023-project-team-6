package com.example.speechbuddy.compose.symbolselection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.ui.SpeechBuddyTheme

/**
 * Custom UI designed for Symbol
 *
 * @param onValueChange the callback that is triggered when the input service updates the text. An updated text comes as a parameter of the callback
 * @param modifier the Modifier to be applied to this text field
 * @param isFavorite indicates if the symbol is favorite
 */
@Composable
fun Symbol(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    isFavorite: Boolean = false
) {
    OutlinedCard(
        modifier = Modifier
            .size(width = 140.dp, height = 140.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
    ) {
        Image(
            painter = if (isFavorite) {
                painterResource(id = R.drawable.fav_star_selected)
            } else {
                painterResource(id = R.drawable.fav_star)
            },
            modifier = Modifier
                .padding(start = 4.dp, top = 4.dp)
                .align(Alignment.Start)
                .size(16.dp),
            contentDescription = "favorite star",
        )

        OutlinedCard(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 100.dp, height = 80.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(80.dp),
                imageVector = Icons.Default.DateRange,
                contentDescription = "symbol image",
            )
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "119에 전화를 걸어주시기 바랍니다. ",
                modifier = Modifier
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SymbolPreview() {
    SpeechBuddyTheme {
        Symbol(
            isFavorite = true,
            onValueChange = {},
        )
    }
}