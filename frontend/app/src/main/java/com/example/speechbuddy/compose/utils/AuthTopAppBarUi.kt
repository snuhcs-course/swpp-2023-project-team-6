package com.example.speechbuddy.compose.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
 * Custom UI designed for top app bars used in auth activity.
 *
 * @param modifier the Modifier to be applied to this top app bar
 * @param onBackClick called when the back icon of this top app bar is clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTopAppBarUi(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    /*
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Image(
                painter = painterResource(id = R.drawable.top_app_bar_ic),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier.size(148.dp),
                contentScale = ContentScale.Fit
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        },
    )
     */
}

@Preview
@Composable
fun AuthTopAppBarUiPreview() {
    SpeechBuddyTheme {
        AuthTopAppBarUi(onBackClick = {})
    }
}
