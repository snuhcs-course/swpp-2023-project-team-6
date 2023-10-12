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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.ui.SpeechBuddyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTopAppBarUi(
    onPreviousClick: ()->Unit
){
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.top_app_bar_ic),
                contentDescription = "app logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(148.dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                /*TODO*/
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go back to landing page"
                )
            }
        },
//                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    )
}

@Preview
@Composable
fun LoginTopAppBarUiPreview(){
    SpeechBuddyTheme {
        LoginTopAppBarUi(onPreviousClick = {})
    }
}

