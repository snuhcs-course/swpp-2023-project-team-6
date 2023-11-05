package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.HomeTopAppBarUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySymbolSettings(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    bottomPaddingValues: PaddingValues
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                HomeTopAppBarUi(
                    title = stringResource(id = R.string.settings),
                    onBackClick = onBackClick,
                    isBackClickEnabled = true
                )
            }
        ) { topPaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = topPaddingValues.calculateTopPadding(),
                        bottom = bottomPaddingValues.calculateBottomPadding()
                    )
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                /* TODO */
                Text(text = "My Symbol Settings")
            }
        }
    }
}