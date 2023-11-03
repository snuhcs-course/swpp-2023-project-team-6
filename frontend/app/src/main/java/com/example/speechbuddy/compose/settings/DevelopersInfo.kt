package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.HomeTopAppBarUi
import com.example.speechbuddy.compose.utils.TitleUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevelopersInfo(
    modifier: Modifier = Modifier, onBackClick: () -> Unit, bottomPaddingValues: PaddingValues
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(topBar = {
            HomeTopAppBarUi(
                title = stringResource(id = R.string.settings),
                onBackClick = onBackClick,
                isBackClickEnabled = true
            )
        }) { topPaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = topPaddingValues.calculateTopPadding(),
                        bottom = bottomPaddingValues.calculateBottomPadding()
                    )
                    .padding(24.dp), verticalArrangement = Arrangement.Center
            ) {
                TitleUi(title = stringResource(id = R.string.developers_info))

                Spacer(modifier = modifier.height(20.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    /* TODO */
                    SettingsRow(label = "서울대학교 소개원실 team 6")
                    SettingsRow(label = "김연정")
                    SettingsRow(label = "류명현")
                    SettingsRow(label = "오준형")
                    SettingsRow(label = "이민영")
                    SettingsRow(label = "이석찬")
                    SettingsRow(label = "주승민")
                }
            }
        }
    }
}