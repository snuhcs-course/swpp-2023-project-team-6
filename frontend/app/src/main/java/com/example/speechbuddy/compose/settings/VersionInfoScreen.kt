package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.TitleUi

@Composable
fun VersionInfo(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TitleUi(title = stringResource(id = R.string.version_info))

            Spacer(modifier = modifier.height(20.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SettingsRow(
                    label = stringResource(id = R.string.version),
                    content = {
                        /* TODO */
                        SettingsRowText(text = "1.0.0")
                    }
                )

                SettingsRow(
                    label = stringResource(id = R.string.developers_email),
                    content = {
                        /* TODO */
                        SettingsRowText(text = "speechbuddy@gmail.com")
                    }
                )
            }
        }
    }
}