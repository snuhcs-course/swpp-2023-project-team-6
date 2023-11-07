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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.utils.Constants.Companion.CATEGORY_IMAGE_PATH

/**
 * Custom UI designed for Category
 *
 * @param category data class Category to be passed to the UI
 * @param modifier the Modifier to be applied to this outlined card
 * @param onSelect called when this Category is clicked
 */
@OptIn(ExperimentalGlideComposeApi::class)
@ExperimentalMaterial3Api
@Composable
fun CategoryUi(
    category: Category, modifier: Modifier = Modifier, onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        modifier = modifier.size(140.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Box {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlideImage(
                    model = CATEGORY_IMAGE_PATH.plus("category_${category.id}.png"),
                    contentDescription = category.text,
                    modifier = Modifier.height(95.dp),
                    contentScale = ContentScale.FillHeight
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(color = MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(horizontal = 10.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.text,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
