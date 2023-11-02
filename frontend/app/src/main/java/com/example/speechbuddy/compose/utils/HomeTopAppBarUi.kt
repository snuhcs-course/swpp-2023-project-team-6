package com.example.speechbuddy.compose.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.ui.SpeechBuddyTheme

/**
 * Custom UI designed for top app bars used in main activity.
 *
 * @param title title to be displayed in the center of this top app bar
 * @param modifier the Modifier to be applied to this top app bar
 * @param onBackClick called when the back icon of this top app bar is clicked
 * @param isBackClickEnabled decides what to show in the left edge of this top app bar. If false, app logo is displayed instead.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBarUi(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    isBackClickEnabled: Boolean = false
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            if (isBackClickEnabled)
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Go back to landing page"
                    )
                }
            else
                Image(
                    painter = painterResource(id = R.drawable.stop_icon),
                    contentDescription = stringResource(id = R.string.app_name),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(40.dp)
                )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    )
}

@Preview
@Composable
fun HomeTopAppBarUiPreview() {
    SpeechBuddyTheme {
        HomeTopAppBarUi(title = "title", onBackClick = {})
    }
}
