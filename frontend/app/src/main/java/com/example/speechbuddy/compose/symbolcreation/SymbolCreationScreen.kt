package com.example.speechbuddy.compose.symbolcreation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.utils.DropdownTextFieldUi
import com.example.speechbuddy.compose.utils.HomeTopAppBarUi
import com.example.speechbuddy.compose.utils.TitleUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolCreationScreen(
    modifier: Modifier = Modifier,
    bottomPaddingValues: PaddingValues
) {
    val myItemsList = listOf(
        "Option 1", "Option 2", "Option 3", "Option 4", "Option 5", "Option 6",
        "Option 7", "Option 8", "Option 9", "Option 10", "Option 11", "Option 12",
        "Option 1", "Option 2", "Option 3", "Option 4", "Option 5", "Option 6",
        "Option 7", "Option 8", "Option 9", "Option 10", "Option 11", "Option 12"
    )

    // State to hold the currently selected value
    var selectedValue by remember { mutableStateOf("") }
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                HomeTopAppBarUi(title = stringResource(id = R.string.symbol_creation))
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
                    title = "상징 추가하기",
                    description = "소개원실 좋아요"
                )

                Spacer(modifier = Modifier.height(20.dp))


                // ... other code ...

                DropdownTextFieldUi(
                    selectedValue = selectedValue,
                    onValueChange = { newValue -> selectedValue = newValue },
                    items = myItemsList,
                    modifier = Modifier.padding(16.dp),
                    label = { Text("Choose an option") },
                    isError = false,
                    isValid = true
                )
            }
        }
    }
}