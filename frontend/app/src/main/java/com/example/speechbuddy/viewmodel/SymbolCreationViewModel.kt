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
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.repository.SymbolCreationRepository
import com.example.speechbuddy.repository.SymbolRepository
import com.example.speechbuddy.ui.models.SymbolCreationError
import com.example.speechbuddy.ui.models.SymbolCreationErrorType
import com.example.speechbuddy.ui.models.SymbolCreationUiState
import com.example.speechbuddy.utils.isValidSymbolText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val repository: SymbolCreationRepository,
    private val local_repository: SymbolRepository
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
        if (_uiState.value.error?.type==SymbolCreationErrorType.PHOTO_INPUT) validatePhotoInput()
        if(photoInputUri!=null) photoInputBitmap = uriToBitmap(photoInputUri, context)
    }

    fun setSymbolText(input: String){
        symbolTextInput = input
        if (_uiState.value.error?.type==SymbolCreationErrorType.SYMBOL_TEXT) validateSymbolText()
    }

    fun setCategory(input: String){
        categoryInput = input
        if (_uiState.value.error?.type==SymbolCreationErrorType.CATEGORY) validateCategory()
    }

    private fun validatePhotoInput(){
        if(photoInputUri!=null){
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPhotoInput = true,
                    error = null
                )
            }
        }
    }

    private fun validateCategory(){
        if (categoryInput.isNotBlank()){
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

    fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
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

    fun createSymbol(context:Context){
//        var categoryId = 0
//         category Id processing
//        viewModelScope.launch {
//            local_repository.getCategories(categoryInput).collect { categories ->
//                if (categories.isNotEmpty()) {
//                    val category = categories.first()
//                    categoryId = category.id
//                }
//            }
//        }
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
//        } else if(categoryId<1 || categoryId >24){
//            _uiState.update { currentState ->
//                currentState.copy(
//                    isValidCategory = false,
//                    error = SymbolCreationError(
//                        type = SymbolCreationErrorType.CATEGORY,
//                        messageId = R.string.wrong_category
//                    )
//                )
//            }
        }
        else if(photoInputUri==null || photoInputBitmap==null){
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
            val symbol = Symbol(
                id = 503,
                text = "test",
                imageUrl = "https://speechbuddy-bucket.s3.ap-northeast-2.amazonaws.com/media/symbol/user_25/202311081c9b0f83b83b4868912f11a4c4f81c85.jpeg",
                categoryId = 1,
                isFavorite = false,
                isMine = true
            )
            viewModelScope.launch { local_repository.insertSymbol(symbol) }


            // file processing
//            val uniqueFileName = "symbol_${System.currentTimeMillis()}"
//            val imageFile = bitmapToFile(context, photoInputBitmap!!, uniqueFileName)
//            val imagePart = fileToMultipartBodyPart(imageFile, "image")

//            viewModelScope.launch(Dispatchers.IO) {
//                repository.createSymbolBackup(
//                    symbolText = symbolTextInput,
//                    categoryId = categoryId,
//                    image = imagePart
//                ).collect{
//                    if(it.status == Resource(Status.SUCCESS, "", "").status){
//                        // Store new symbol in local db
//                        val symbolId = it.data!!.id
//                        val imageUrl = it.data!!.imageUrl
//                        val symbol = Symbol(
//                            id = 503,
//                            text = "test",
//                            imageUrl = "https://speechbuddy-bucket.s3.ap-northeast-2.amazonaws.com/media/symbol/user_25/202311081c9b0f83b83b4868912f11a4c4f81c85.jpeg",
//                            categoryId = 1,
//                            isFavorite = false,
//                            isMine = true
//                        )
//                        local_repository.insertSymbol(symbol)
//                    } else if (it.message?.contains("Unknown")==true){
//                        _uiState.update { currentState ->
//                            currentState.copy(
//                                isValidSymbolText = false,
//                                error = SymbolCreationError(
//                                    type = SymbolCreationErrorType.SYMBOL_TEXT,
//                                    messageId = R.string.internet_error
//                                )
//                            )
//                        }
//                    }
//                }
//            }
        }
    }
}