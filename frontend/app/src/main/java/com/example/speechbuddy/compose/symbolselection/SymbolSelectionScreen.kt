package com.example.speechbuddy.compose.symbolselection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SymbolSelectionScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Talk With Symbol")
    }
}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TalkWithSymbolScreen(
//
//) {
//    Surface(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(25.dp)
//                .fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center,
//        ) {
//            TitleUi(
//                title = "상징으로 말하기",
//                description = "소개원실 싫어요"
//            )
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            OutlinedTextField(
//                value = "",
//                onValueChange = {},
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .defaultMinSize(minHeight = 300.dp),
//                textStyle = MaterialTheme.typography.bodyMedium,
//                shape = RoundedCornerShape(10.dp)
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//private fun TalkWithSymbolPreview() {
//    SpeechBuddyTheme {
//        TalkWithSymbolScreen()
//    }
//}