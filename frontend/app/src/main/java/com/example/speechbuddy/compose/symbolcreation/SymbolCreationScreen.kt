package com.example.speechbuddy.compose.symbolcreation

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TextFieldUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.ui.models.DialogState
import com.example.speechbuddy.ui.models.PhotoType
import com.example.speechbuddy.ui.models.SymbolCreationErrorType
import com.example.speechbuddy.ui.models.SymbolCreationUiState
import com.example.speechbuddy.ui.models.ToastState
import com.example.speechbuddy.utils.Constants
import com.example.speechbuddy.viewmodel.SymbolCreationViewModel

@Composable
fun SymbolCreationScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    viewModel: SymbolCreationViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.observeAsState(emptyList())

    val isSymbolTextError = uiState.error?.type == SymbolCreationErrorType.SYMBOL_TEXT
    val isCategoryError = uiState.error?.type == SymbolCreationErrorType.CATEGORY
    val isPhotoInputError = uiState.error?.type == SymbolCreationErrorType.PHOTO_INPUT
    val isConnectionError = uiState.error?.type == SymbolCreationErrorType.CONNECTION

    val creationResultMessage by viewModel.creationResultMessage.observeAsState()

    // Get photo from gallery
    val galleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            viewModel.setPhotoInputUri(uri, context)
        }
    // Take photo
    val cameraLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            bitmap?.let {
                viewModel.updatePhotoInputBitmap(bitmap)
            }
        }
    // Request permission launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            viewModel.updateToastState("show")
        }
    }

    if (viewModel.toastState == ToastState.SHOW) {
        Toast.makeText(
            context,
            stringResource(id = R.string.camera_permission_description),
            Toast.LENGTH_LONG
        ).show()
        viewModel.updateToastState("hide")
    }

    if (creationResultMessage != null) {
        LaunchedEffect(key1 = creationResultMessage) {
            val toastMessage = context.resources.getString(creationResultMessage!!)
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
        }
    }

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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleUi(
                title = stringResource(id = R.string.create_new_symbol),
                description = stringResource(id = R.string.symbol_creation_description)
            )

            Spacer(modifier = Modifier.height(30.dp))

            AddPhotoButton(
                onClick = { viewModel.updateDialogState("show") },
                isError = isPhotoInputError,
                viewModel = viewModel
            )

            if (viewModel.dialogState == DialogState.SHOW) {
                PhotoOptionDialog(
                    onDismissRequest = { viewModel.updateDialogState("hide") },
                    onCameraClick = {
                        // Check if permission is already granted
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.CAMERA
                            ) -> {
                                cameraLauncher.launch(null)
                            }

                            else -> {
                                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        }
                        viewModel.photoType = PhotoType.CAMERA
                    },
                    onGalleryClick = {
                        galleryLauncher.launch("image/*")
                        viewModel.photoType = PhotoType.GALLERY
                    },
                    onCancelClick = { viewModel.updateDialogState("hide") }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Symbol Category Field
            DropdownUi(
                selectedValue = viewModel.categoryInput,
                onValueChange = { viewModel.setCategory(it) },
                items = categories,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.category)) },
                isError = isCategoryError,
                viewModel = viewModel,
                uiState = uiState
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Symbol Text Field
            TextFieldUi(
                value = viewModel.symbolTextInput,
                onValueChange = { viewModel.setSymbolText(it) },
                label = { Text(stringResource(R.string.new_symbol_name)) },
                supportingText = {
                    if (isSymbolTextError || isConnectionError) {
                        Text(stringResource(id = uiState.error!!.messageId))
                    }
                },
                isError = isSymbolTextError || isConnectionError,
                isValid = uiState.isValidSymbolText
            )

            // Symbol Creation Button
            ButtonUi(
                text = stringResource(id = R.string.create),
                onClick = { viewModel.createSymbol(context) },
                modifier = Modifier.offset(y = 50.dp),
                isEnabled = uiState.buttonEnabled,
                isError = false
            )
        }
    }

    uiState.loading.let { loading ->
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }
    }
}

@Composable
private fun DropdownUi(
    selectedValue: Category?,
    onValueChange: (Category) -> Unit,
    items: List<Category>,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    viewModel: SymbolCreationViewModel,
    uiState: SymbolCreationUiState
) {
    Column {
        Box(modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(10.dp)
            )
            .clickable { viewModel.expandCategory() }
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (selectedValue != null) {
                    Text(
                        text = selectedValue.text,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    label?.invoke() ?: Text(
                        text = stringResource(id = R.string.choose_a_category),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.dropdown)
                )
            }
        }

        DropdownMenu(
            expanded = uiState.isCategoryExpanded,
            onDismissRequest = { viewModel.dismissCategory() },
            modifier = Modifier
                .width(300.dp)
                .heightIn(max = 230.dp)
        ) {
            items.forEach { item ->
                DropdownMenuItem(text = {
                    Text(
                        text = item.text,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }, onClick = {
                    onValueChange(item)
                    viewModel.dismissCategory()
                })
            }
        }
    }
}

@Composable
private fun AddPhotoButton(
    onClick: () -> Unit,
    isError: Boolean = false,
    viewModel: SymbolCreationViewModel
) {
    val color =
        if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(140.dp)
            .clickable(onClick = onClick)
            .border(
                border = BorderStroke(1.dp, color), shape = RoundedCornerShape(10.dp)
            )
    ) {
        if (viewModel.photoInputBitmap != null) {
            SymbolPreview(
                bitmap = viewModel.photoInputBitmap!!.asImageBitmap(),
                text = viewModel.symbolTextInput
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.outline_add_a_photo_24),
                contentDescription = stringResource(R.string.create_new_symbol)
            )
        }
    }
}

@Composable
fun PhotoOptionDialog(
    onDismissRequest: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {},
        dismissButton = {},
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.choose_photo_option),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PhotoOptionButton(
                    onClick = {
                        onCameraClick()
                        onDismissRequest()
                    },
                    text = stringResource(id = R.string.take_photo),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                PhotoOptionButton(
                    onClick = {
                        onGalleryClick()
                        onDismissRequest()
                    },
                    text = stringResource(id = R.string.choose_from_gallery),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                PhotoOptionButton(
                    onClick = {
                        onCancelClick()
                        onDismissRequest()
                    },
                    text = stringResource(id = R.string.cancel),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    )
}

@Composable
private fun PhotoOptionButton(
    onClick: () -> Unit,
    text: String,
    colors: ButtonColors
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = colors
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun SymbolPreview(
    bitmap: ImageBitmap,
    text: String
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    bitmap = bitmap,
                    contentDescription = stringResource(R.string.new_symbol_photo),
                    modifier = Modifier
                        .height(95.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(color = MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        textAlign = TextAlign.Center,
                        maxLines = Constants.MAXIMUM_LINES_FOR_SYMBOL_TEXT,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}