package com.example.speechbuddy.compose.texttospeach

import android.text.method.ScrollingMovementMethod
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.viewmodel.TextToSpeechViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextToSpeechScreen(
    viewModel: TextToSpeechViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(25.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            TitleUi(
                title = stringResource(id = R.string.tts_text),
                description = stringResource(id = R.string.tts_explain)
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = viewModel.textInput,
                onValueChange = {
                    viewModel.setText(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 300.dp)
                    .verticalScroll(rememberScrollState())
                    .height(300.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Play button
            Button(
                onClick = {
                    viewModel.ttsStart(context)
                },
                enabled = uiState.isPlayEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text(
                    style = MaterialTheme.typography.headlineMedium,
                    text = stringResource(id = R.string.play_text)
                )
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = stringResource(id = R.string.play_text),
                    modifier = Modifier.size(36.dp)
                )
            }

            // Stop button
            Button(
                onClick = {
                    viewModel.ttsStop()
                },
                enabled = uiState.isStopEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text(
                    style = MaterialTheme.typography.headlineMedium,
                    text = stringResource(id = R.string.stop_text)
                )
                Icon(
                    painterResource(R.drawable.stop_icon),
                    contentDescription = stringResource(id = R.string.pause_text),
                    modifier = Modifier.size(36.dp)
                )
            }
        }

    }

}
