package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.viewmodel.AccountSettingsViewModel
import com.example.speechbuddy.viewmodel.GuideScreenViewModel

@Composable
fun GuestSettings(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    viewModel: AccountSettingsViewModel = hiltViewModel()
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
            Column(
                modifier = Modifier.height(240.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                TitleUi(
                    title = stringResource(id = R.string.account),
                    description = stringResource(id = R.string.guest_mode_description)
                )

                ButtonUi(
                    text = stringResource(id = R.string.exit_guest_mode),
                    onClick = { viewModel.exitGuestMode() }
                )
            }
        }
    }
}