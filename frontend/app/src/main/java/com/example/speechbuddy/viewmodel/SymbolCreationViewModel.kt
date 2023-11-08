package com.example.speechbuddy.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.R
import com.example.speechbuddy.repository.SymbolCreationRepository
import com.example.speechbuddy.ui.models.SymbolCreationError
import com.example.speechbuddy.ui.models.SymbolCreationErrorType
import com.example.speechbuddy.ui.models.SymbolCreationUiState
import com.example.speechbuddy.utils.Constants
import com.example.speechbuddy.utils.isValidSymbolText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SymbolCreationViewModel @Inject internal constructor(
    private val repository: SymbolCreationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SymbolCreationUiState())
    val uiState: StateFlow<SymbolCreationUiState> = _uiState.asStateFlow()

    var photoInputUri by mutableStateOf<Uri?>(null)

    var photoInputBitmap by mutableStateOf<Bitmap?>(null)

    var symbolTextInput by mutableStateOf("")
        private set

    var categoryInput by mutableStateOf("")
        private set

    @JvmName("callFromUri")
    fun setPhotoInputUri(input: Uri?, context: Context){
        photoInputUri = input
        if(photoInputUri!=null) photoInputBitmap = convertUriToBitmap(photoInputUri, context)
    }

    fun setSymbolText(input: String){
        symbolTextInput = input
        if (_uiState.value.error?.type==SymbolCreationErrorType.SYMBOL_TEXT) validateSymbolText()
    }

    fun setCategory(input: String){
        categoryInput = input
    }

    private fun validateSymbolText() {
        if (isValidSymbolText(symbolTextInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidSymbolText = true,
                    error = null
                )
            }
        }
    }

    @Suppress("DEPRECATION", "NewApi")
    private fun convertUriToBitmap(uri: Uri?, context: Context): Bitmap {
        return when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // 28
            true -> {
                val source = ImageDecoder.createSource(context.contentResolver, uri!!)
                ImageDecoder.decodeBitmap(source)
            }
            else -> {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        }
    }

    fun createSymbol(){
        if (!isValidSymbolText(symbolTextInput)){
            _uiState.update { currentState ->
                currentState.copy(
                    isValidSymbolText = false,
                    error = SymbolCreationError(
                        type = SymbolCreationErrorType.SYMBOL_TEXT,
                        messageId = R.string.invalid_new_symbol_text
                    )
                )
            }
        } else if(categoryInput.isBlank()){
            _uiState.update { currentState ->
                currentState.copy(
                    isValidCategory = false,
                    error = SymbolCreationError(
                        type = SymbolCreationErrorType.CATEGORY,
                        messageId = R.string.empty_category
                    )
                )
            }
        } else if(photoInputUri==null || photoInputBitmap==null){
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPhotoInput = false,
                    error = SymbolCreationError(
                        type = SymbolCreationErrorType.PHOTO_INPUT,
                        messageId = R.string.empty_photo
                    )
                )
            }
        } else {
            viewModelScope.launch {
                repository.createSymbolBackup(
                    symbolText = symbolTextInput,
                    categoryId = 1 /*To be changed*/,
                    image = , /*To be changed*/
                ).collect{

                }
            }
        }
    }
}