package com.example.speechbuddy.viewmodel

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.speechbuddy.ui.models.ButtonStatusType
import com.example.speechbuddy.ui.models.TextToSpeechUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TextToSpeechViewModel @Inject internal constructor(
) : ViewModel() {

    private val _uiState = MutableStateFlow(TextToSpeechUiState())
    val uiState: StateFlow<TextToSpeechUiState> = _uiState.asStateFlow()

    private var textToSpeech: TextToSpeech? = null

    var textInput by mutableStateOf("")
        private set

    fun setText(input: String) {
        textInput = input
    }

private fun clearText() {
    textInput = ""
}

    fun ttsStop() {
        textToSpeech?.stop()
    }

    fun ttsStart(context: Context) {
        // disable button
        _uiState.update { currentState ->
            currentState.copy(
                buttonStatus = ButtonStatusType.STOP
            )
        }

        textToSpeech = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                textToSpeech?.let { txtToSpeech ->
                    txtToSpeech.language = Locale.KOREAN
                    txtToSpeech.setSpeechRate(1.0f)

                    val params = Bundle()
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "")
                    txtToSpeech.setOnUtteranceProgressListener(object :
                        UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            // Speech has started, button is already disabled
                        }

                        override fun onStop(utteranceId: String?, interrupted: Boolean) {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    buttonStatus = ButtonStatusType.PLAY
                                )
                            }
                        }

                        override fun onDone(utteranceId: String?) {
                            // Speech has finished, re-enable the button
                            _uiState.update { currentState ->
                                currentState.copy(
                                    buttonStatus = ButtonStatusType.PLAY
                                )
                            }
                            clearText()
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            // There was an error, re-enable the button
                            _uiState.update { currentState ->
                                currentState.copy(
                                    buttonStatus = ButtonStatusType.PLAY
                                )
                            }
                        }
                    })

                    txtToSpeech.speak(
                        textInput,
                        TextToSpeech.QUEUE_ADD,
                        params,
                        "UniqueID"
                    )
                }
            } else {
                // Initialization failed, re-enable the button
                _uiState.update { currentState ->
                    currentState.copy(
                        buttonStatus = ButtonStatusType.PLAY
                    )
                }
            }
        }
    }

}