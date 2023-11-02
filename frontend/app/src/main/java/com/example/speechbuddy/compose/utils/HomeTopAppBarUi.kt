package com.example.speechbuddy.compose.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
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
 * @param actions the actions displayed at the end of the top app bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBarUi(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    isBackClickEnabled: Boolean = false,
    actions: @Composable (RowScope.() -> Unit) = {}
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
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            else
                Image(
                    painter = painterResource(id = R.drawable.speechbuddy_parrot),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier
                        .padding(6.dp)
                        .size(40.dp),
                    contentScale = ContentScale.Fit
                )
        },
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
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