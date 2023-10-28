package com.example.speechbuddy.viewmodel

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.speechbuddy.ui.models.TextToSpeechUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TextToSpeechViewModel @Inject internal constructor(
) : ViewModel() {

    private val _state = MutableStateFlow(TextToSpeechUiState())
    val uiState: StateFlow<TextToSpeechUiState> = _state.asStateFlow()

    private var textToSpeech: TextToSpeech? = null

    var textInput by mutableStateOf("")
        private set

    fun setText(input: String) {
        textInput = input
    }

    fun ttsStop(){
        textToSpeech?.stop()
    }

    fun ttsStart(context: Context) {
        // disable button
        _state.value = uiState.value.copy(
            isPlayEnabled = false,
            isStopEnabled = true
        )
        textToSpeech = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                textToSpeech?.let { txtToSpeech ->
                    txtToSpeech.language = Locale.KOREAN
                    txtToSpeech.setSpeechRate(1.0f)

                    val params = Bundle()
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "")
                    txtToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            // Speech has started, button is already disabled
                        }

                        override fun onStop(utteranceId: String?, interrupted: Boolean) {
                            _state.value = uiState.value.copy(
                                isPlayEnabled = true,
                                isStopEnabled = false
                            )
                        }

                        override fun onDone(utteranceId: String?) {
                            // Speech has finished, re-enable the button
                            _state.value = uiState.value.copy(
                                isPlayEnabled = true,
                                isStopEnabled = false
                            )
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            // There was an error, re-enable the button
                            _state.value = uiState.value.copy(
                                isPlayEnabled = true,
                                isStopEnabled = false
                            )
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
                _state.value = uiState.value.copy(
                    isPlayEnabled = true
                )
            }
        }
    }

}