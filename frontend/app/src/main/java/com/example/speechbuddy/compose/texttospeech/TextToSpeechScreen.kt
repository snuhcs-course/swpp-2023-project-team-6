package com.example.speechbuddy.compose.texttospeech

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.TopAppBarUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.ui.models.ButtonStatusType
import com.example.speechbuddy.viewmodel.TextToSpeechViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextToSpeechScreen(
    modifier: Modifier = Modifier,
    bottomPaddingValues: PaddingValues,
    viewModel: TextToSpeechViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(topBar = {
            TopAppBarUi(title = stringResource(id = R.string.talk_with_speech))
        }) { topPaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = topPaddingValues.calculateTopPadding(),
                        bottom = bottomPaddingValues.calculateBottomPadding()
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                TitleUi(
                    title = stringResource(id = R.string.talk_with_sound),
                    description = stringResource(id = R.string.tts_description)
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = viewModel.textInput,
                    onValueChange = {
                        viewModel.setText(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .height(300.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                TextToSpeechButton(buttonStatus = uiState.buttonStatus,
                    onPlay = { viewModel.ttsStart(context) },
                    onStop = { viewModel.ttsStop() })
            }
        }
    }
}

@Composable
private fun TextToSpeechButton(
    buttonStatus: ButtonStatusType, onPlay: () -> Unit, onStop: () -> Unit
) {
    val textToSpeechButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.onBackground
    )

    when (buttonStatus) {
        ButtonStatusType.PLAY -> Button(
            onClick = onPlay, colors = textToSpeechButtonColors
        ) {
            Text(
                style = MaterialTheme.typography.headlineMedium,
                text = stringResource(id = R.string.play)
            )
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = stringResource(id = R.string.play),
                modifier = Modifier.size(36.dp)
            )
        }

        ButtonStatusType.STOP -> Button(
            onClick = onStop, colors = textToSpeechButtonColors
        ) {
            Text(
                style = MaterialTheme.typography.headlineMedium,
                text = stringResource(id = R.string.stop)
            )
            Icon(
                painterResource(R.drawable.stop_icon),
                contentDescription = stringResource(id = R.string.stop),
                modifier = Modifier.size(36.dp)
            )
        }
    }
}