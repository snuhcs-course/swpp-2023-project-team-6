package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.compose.utils.TopAppBarUi
import com.example.speechbuddy.viewmodel.AccountSettingsViewModel
import com.example.speechbuddy.viewmodel.GuideScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestSettings(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    bottomPaddingValues: PaddingValues,
    viewModel: AccountSettingsViewModel = hiltViewModel(),
    guideScreenViewModel: GuideScreenViewModel
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBarUi(
                    title = stringResource(id = R.string.settings),
                    onBackClick = onBackClick,
                    isBackClickEnabled = true,
                    guideScreenViewModel = guideScreenViewModel
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
                verticalArrangement = Arrangement.Center
            ) {
                TitleUi(
                    title = stringResource(id = R.string.account),
                    description = stringResource(id = R.string.guest_mode_description)
                )

                ButtonUi(
                    text = stringResource(id = R.string.exit_guest_mode),
                    onClick = { viewModel.exitGuestMode() },
                    modifier = Modifier.offset(y = 240.dp)
                )

                Spacer(modifier = Modifier.padding(70.dp))
            }
        }
    }
}