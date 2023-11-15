package com.example.speechbuddy.compose.texttospeech

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
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
import com.example.speechbuddy.compose.utils.HomeTopAppBarUi
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

    val activatedColor = MaterialTheme.colorScheme.onBackground
    val deactivatedColor = MaterialTheme.colorScheme.outline

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(topBar = {
            HomeTopAppBarUi(title = stringResource(id = R.string.talk_with_speech))
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
                        .verticalScroll(rememberScrollState())
                        .height(200.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    TextToSpeechButton(
                        buttonStatus = uiState.buttonStatus,
                        activatedColor = activatedColor,
                        deactivatedColor = deactivatedColor,
                        onPlay = { viewModel.ttsStart(context) },
                        onStop = { viewModel.ttsStop() },
                        isTextEmpty = viewModel.textInput.isEmpty()
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    TextClearButton(
                        buttonStatus = uiState.buttonStatus,
                        activatedColor = activatedColor,
                        deactivatedColor = deactivatedColor,
                        onClick = { viewModel.clearText() },
                        isTextEmpty = viewModel.textInput.isEmpty()
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TextToSpeechButton(
    buttonStatus: ButtonStatusType,
    activatedColor: Color,
    deactivatedColor: Color,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    isTextEmpty: Boolean
) {
    when (buttonStatus) {
        ButtonStatusType.PLAY -> {
            Row(
                modifier = Modifier
                    .clickable(onClick = { if (!isTextEmpty) onPlay() })
                    .height(38.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.play_text),
                    color = if (isTextEmpty) deactivatedColor else activatedColor,
                    style = MaterialTheme.typography.headlineSmall
                )
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = stringResource(id = R.string.play_text),
                    modifier = Modifier.size(38.dp),
                    tint = if (isTextEmpty) deactivatedColor else activatedColor
                )
            }
        }

        ButtonStatusType.STOP -> {
            Row(
                modifier = Modifier
                    .clickable(onClick = onStop)
                    .height(38.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.stop_text),
                    color = activatedColor,
                    style = MaterialTheme.typography.headlineSmall
                )
                Icon(
                    painter = painterResource(R.drawable.stop_icon),
                    contentDescription = stringResource(id = R.string.stop_text),
                    modifier = Modifier.size(38.dp),
                    tint = activatedColor
                )
            }
        }
    }
}

@Composable
fun TextClearButton(
    buttonStatus: ButtonStatusType,
    activatedColor: Color,
    deactivatedColor: Color,
    onClick: () -> Unit,
    isTextEmpty: Boolean
) {
    val isActivated = buttonStatus == ButtonStatusType.PLAY && !isTextEmpty

    Row(
        modifier = Modifier
            .clickable(onClick = { if (isActivated) onClick() })
            .height(38.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.clear_text),
            color = if (isActivated) activatedColor else deactivatedColor,
            style = MaterialTheme.typography.headlineSmall
        )
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(id = R.string.clear_text),
            modifier = Modifier.size(38.dp),
            tint = if (isActivated) activatedColor else deactivatedColor
        )
    }
}