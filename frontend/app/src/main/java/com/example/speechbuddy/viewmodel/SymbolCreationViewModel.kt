package com.example.speechbuddy.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.R
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.repository.SymbolRepository
import com.example.speechbuddy.repository.WeightTableRepository
import com.example.speechbuddy.ui.models.DialogState
import com.example.speechbuddy.ui.models.PhotoType
import com.example.speechbuddy.ui.models.SymbolCreationError
import com.example.speechbuddy.ui.models.SymbolCreationErrorType
import com.example.speechbuddy.ui.models.SymbolCreationUiState
import com.example.speechbuddy.utils.Status
import com.example.speechbuddy.utils.isValidSymbolText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class SymbolCreationViewModel @Inject internal constructor(
    private val weightTableRepository: WeightTableRepository,
    private val repository: SymbolRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SymbolCreationUiState())
    val uiState: StateFlow<SymbolCreationUiState> = _uiState.asStateFlow()

    private val _creationResultMessage = MutableLiveData<Int>()
    val creationResultMessage: LiveData<Int> = _creationResultMessage

    val categories = repository.getAllCategories().asLiveData()

    var photoInputUri by mutableStateOf<Uri?>(null)

    var photoInputBitmap by mutableStateOf<Bitmap?>(null)

    var photoType by mutableStateOf<PhotoType?>(null)

    var dialogState by mutableStateOf<DialogState?>(DialogState.HIDE)

    var symbolTextInput by mutableStateOf("")
        private set

    var categoryInput by mutableStateOf<Category?>(null)
        private set

    fun updateDialogState(updateState: String) {
        when (updateState) {
            "show" -> {
                dialogState = DialogState.SHOW
            }

            "hide" -> {
                dialogState = DialogState.HIDE
            }
        }
    }

    fun expandCategory() {
        _uiState.update { currentState ->
            currentState.copy(
                isCategoryExpanded = true
            )
        }
    }

    fun dismissCategory() {
        _uiState.update { currentState ->
            currentState.copy(
                isCategoryExpanded = false
            )
        }
    }

    @JvmName("callFromUri")
    fun setPhotoInputUri(input: Uri?, context: Context) {
        photoInputUri = input
        if (_uiState.value.error?.type == SymbolCreationErrorType.PHOTO_INPUT) validatePhotoInput()
        if (photoInputUri != null) photoInputBitmap = uriToBitmap(photoInputUri, context)
    }

    fun updatePhotoInputBitmap(bitmap: Bitmap) {
        photoInputBitmap = bitmap
        validatePhotoInput()
    }


    fun setSymbolText(input: String) {
        symbolTextInput = input
        if (_uiState.value.error?.type == SymbolCreationErrorType.SYMBOL_TEXT) validateSymbolText()
    }

    fun setCategory(input: Category) {
        categoryInput = input
        if (_uiState.value.error?.type == SymbolCreationErrorType.CATEGORY) validateCategory()
    }

    private fun validatePhotoInput() {
        if (photoInputBitmap != null) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPhotoInput = true,
                    error = null
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPhotoInput = false,
                    error = SymbolCreationError(
                        type = SymbolCreationErrorType.PHOTO_INPUT,
                        messageId = R.string.no_photo
                    )
                )
            }
        }
    }

    private fun validateCategory() {
        if (categoryInput != null) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidCategory = true,
                    error = null
                )
            }
        }
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

    private fun clearInput() {
        symbolTextInput = ""
        categoryInput = null
        photoInputUri = null
        photoInputBitmap = null
    }

    private fun changeLoadingState() {
        _uiState.update { currentState ->
            currentState.copy(
                loading = !currentState.loading,
                buttonEnabled = !currentState.buttonEnabled
            )
        }
    }

    @Suppress("DEPRECATION", "NewApi")
    private fun uriToBitmap(uri: Uri?, context: Context): Bitmap {
        return when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // version 28
            true -> {
                val source = ImageDecoder.createSource(context.contentResolver, uri!!)
                ImageDecoder.decodeBitmap(source)
            }

            else -> {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        }
    }

    private fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
        // Get the app's internal storage directory
        val internalDir = context.filesDir
        val imageFile = File(internalDir, "$fileName.png")

        // Compress the bitmap as PNG and write it to the file
        FileOutputStream(imageFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }

        return imageFile
    }

    private fun fileToMultipartBodyPart(file: File, paramName: String): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaType())
        return MultipartBody.Part.createFormData(paramName, file.name, requestFile)
    }

    private fun changeFileName(oldFileName: String, newFileName: String, context: Context) {
        val internalStorageDir = context.filesDir
        val oldFile = File(internalStorageDir, oldFileName)

        if (oldFile.exists()) {
            val newFile = File(internalStorageDir, newFileName)
            val success = oldFile.renameTo(newFile)
            if (success) {
                Log.d("FileRename", "File name changed from $oldFileName to $newFileName")
            } else {
                Log.e("FileRename", "Failed to change file name")
            }
        } else {
            Log.e("FileRename", "File with the name $oldFileName does not exist")
        }
    }

    fun createSymbol(context: Context) {
        if (!isValidSymbolText(symbolTextInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidSymbolText = false,
                    error = SymbolCreationError(
                        type = SymbolCreationErrorType.SYMBOL_TEXT,
                        messageId = R.string.invalid_new_symbol_text
                    )
                )
            }
        } else if (categoryInput == null) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidCategory = false,
                    error = SymbolCreationError(
                        type = SymbolCreationErrorType.CATEGORY,
                        messageId = R.string.no_category
                    )
                )
            }
        } else if (
            (photoType == PhotoType.GALLERY && photoInputUri == null)
            || (photoType == PhotoType.CAMERA && photoInputBitmap == null)
        ) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPhotoInput = false,
                    error = SymbolCreationError(
                        type = SymbolCreationErrorType.PHOTO_INPUT,
                        messageId = R.string.no_photo
                    )
                )
            }
        } else {
            if (sessionManager.userId.value == GUEST) {
                viewModelScope.launch {
                    val symbolId = repository.getNextSymbolId().first()
                    val fileName = "symbol_${symbolId}"
                    bitmapToFile(context, photoInputBitmap!!, fileName)

                    val symbol = Symbol(
                        id = symbolId,
                        text = symbolTextInput,
                        imageUrl = null,
                        categoryId = categoryInput!!.id,
                        isFavorite = false,
                        isMine = true
                    )
                    // store symbol locally
                    Log.d("guestguest", symbolId.toString())
                    repository.insertSymbol(symbol)
                    weightTableRepository.updateWeightTableForNewSymbol(symbol)// ------------------------------ modified

                    clearInput()
                    // Notify user that the creation was successful
                    _creationResultMessage.postValue(R.string.create_symbol_success)
                }
            } else { // If login-mode
                // file processing
                changeLoadingState()
                val tempFileName = "symbol_${System.currentTimeMillis()}"
                val imageFile = bitmapToFile(context, photoInputBitmap!!, tempFileName)
                val imagePart = fileToMultipartBodyPart(imageFile, "image")
                viewModelScope.launch {
                    repository.createSymbolBackup(
                        symbolText = symbolTextInput,
                        categoryId = categoryInput!!.id,
                        image = imagePart
                    ).collect { resource ->
                        changeLoadingState()
                        if (resource.status == Status.SUCCESS) {
                            // Store new symbol in local db
                            val symbolId = resource.data!!.id
                            val imageUrl = resource.data.imageUrl
                            val symbol = Symbol(
                                id = symbolId!!,
                                text = symbolTextInput,
                                imageUrl = imageUrl,
                                categoryId = categoryInput!!.id,
                                isFavorite = false,
                                isMine = true
                            )
                            // change file name in internal storage
                            changeFileName("$tempFileName.png", "symbol_$symbolId.png", context)
                            // store symbol locally
                            viewModelScope.launch {
                                repository.insertSymbol(symbol)
                                weightTableRepository.updateWeightTableForNewSymbol(symbol) // ----------------------- modified
                            }
                            clearInput()
                            // Notify user that the creation was successful
                            _creationResultMessage.postValue(R.string.create_symbol_success)
                        } else if (resource.message?.contains(
                                "Unknown",
                                ignoreCase = true
                            ) == true
                        ) {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isValidSymbolText = false,
                                    error = SymbolCreationError(
                                        type = SymbolCreationErrorType.CONNECTION,
                                        messageId = R.string.connection_error
                                    )
                                )
                            }
                            _creationResultMessage.postValue(R.string.create_symbol_error)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val GUEST = -1
    }
}