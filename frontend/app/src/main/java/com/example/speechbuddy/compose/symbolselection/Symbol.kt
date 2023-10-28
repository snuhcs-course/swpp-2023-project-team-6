package com.example.speechbuddy.compose.symbolselection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.ui.SpeechBuddyTheme

@Composable
fun Symbol(

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
            painter = painterResource(id = R.drawable.fav_star),
            modifier = Modifier
                .padding(start=4.dp, top = 4.dp)
                .align(Alignment.Start)
                .size(16.dp),
            contentDescription = "favorite star",
            colorFilter = ColorFilter.tint(Color.Red)
        )

        OutlinedCard(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 96.dp, height = 96.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
            ){
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(96.dp),
                imageVector = Icons.Default.DateRange,
                contentDescription ="symbol image",
                )
        }

        Text(
            text = "Outlined",
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.bodyLarge
        )


    }
}


@Preview(showBackground = true)
@Composable
fun SymbolPreview() {
    SpeechBuddyTheme {
        Symbol(

        )
    }
}