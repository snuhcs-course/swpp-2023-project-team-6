package com.example.speechbuddy.compose.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.ui.SpeechBuddyTheme
import com.example.speechbuddy.utils.Constants

/**
 * Custom UI designed for Symbol
 *
 * @param category data class Category to be passed to the UI
 * @param modifier the Modifier to be applied to this text field
 * @param onSelect called when this Symbol is clicked
 */
@ExperimentalMaterial3Api
@Composable
fun CategoryUi(
    category: Category,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit,
) {
    OutlinedCard(
        onClick = onSelect,
        modifier = modifier.size(140.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer)
    ) {
        Spacer(modifier = Modifier.size(20.dp))

        OutlinedCard(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 100.dp, height = 80.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
        ) {
            Image(
                painter = painterResource(id = category.imageResId),
                contentDescription = category.text,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(80.dp)
            )
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category.text,
                textAlign = TextAlign.Center,
                maxLines = Constants.MAXIMUM_LINES_FOR_SYMBOL_TEXT,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@ExperimentalMaterial3Api
@Composable
fun CategoryPreview() {
    val previewCategory = Category(
        id = 0,
        text = "앵무새",
        imageResId = R.drawable.top_app_bar_ic,
    )

    SpeechBuddyTheme {
        CategoryUi(category = previewCategory, onSelect = {})
    }
}