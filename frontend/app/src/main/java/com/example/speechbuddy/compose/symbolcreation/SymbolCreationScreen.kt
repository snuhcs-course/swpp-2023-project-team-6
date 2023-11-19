package com.example.speechbuddy.compose.symbolcreation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.ButtonUi
import com.example.speechbuddy.compose.utils.TextFieldUi
import com.example.speechbuddy.compose.utils.TopAppBarUi
import com.example.speechbuddy.compose.utils.TitleUi
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.ui.models.SymbolCreationErrorType
import com.example.speechbuddy.ui.models.SymbolCreationUiState
import com.example.speechbuddy.utils.Constants
import com.example.speechbuddy.viewmodel.SymbolCreationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolCreationScreen(
    modifier: Modifier = Modifier,
    bottomPaddingValues: PaddingValues,
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

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBarUi(title = stringResource(id = R.string.create_new_symbol))
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleUi(
                    title = stringResource(id = R.string.create_new_symbol),
                    description = stringResource(id = R.string.symbol_creation_description)
                )

                Spacer(modifier = Modifier.height(30.dp))

                AddPhotoButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    isError = isPhotoInputError,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(30.dp))

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

                ButtonUi(
                    text = stringResource(id = R.string.create),
                    onClick = { viewModel.createSymbol(context) },
                    modifier = Modifier.offset(y = 50.dp),
                    isEnabled = true,
                    isError = false
                )
            }
        }
    }
    if (creationResultMessage != null) {
        val toastMessage = stringResource(id = creationResultMessage!!)
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
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
                        "Select an option", color = MaterialTheme.colorScheme.onSurface
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

    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(140.dp)
            .clickable { onClick() }
            .border(
                border = BorderStroke(1.dp, color), shape = RoundedCornerShape(10.dp)
            )) {
        if (viewModel.photoInputBitmap != null) {
            // Display the loaded image if the photo input bitmap is not null
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