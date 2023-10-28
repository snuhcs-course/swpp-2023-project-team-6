package com.example.speechbuddy.compose.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.speechbuddy.R
import com.example.speechbuddy.compose.settings.SettingsScreen
import com.example.speechbuddy.compose.symbolcreation.SymbolCreationScreen
import com.example.speechbuddy.compose.symbolselection.SymbolSelectionScreen
import com.example.speechbuddy.compose.texttospeech.TextToSpeechScreen
import com.example.speechbuddy.ui.SpeechBuddyTheme

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: Int
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(

) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = listOf(
                    BottomNavItem(
                        "SymbolSelection",
                        "symbolselection",
                        R.drawable.outline_touch_app_24
                    ),
                    BottomNavItem(
                        "TTS",
                        "tts",
                        R.drawable.outline_volume_up_24
                    ),
                    BottomNavItem(
                        "CreateSymbol",
                        "createsymbol",
                        R.drawable.outline_add_a_photo_24
                    ),
                    BottomNavItem(
                        "Settings",
                        "settings",
                        R.drawable.outline_settings_24
                    )
                ),
                navController = navController,
                modifier = Modifier
                    .padding(horizontal = 0.dp, vertical = 10.dp)
                    .height(100.dp),
                onItemClick = {
                    navController.navigate(it.route)
                }
            )
        }
    ) {
        HomeScreenNavHost(
            navController = navController
        )
    }
}

@Composable
fun HomeScreenNavHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = "symbolselection") {
        composable("symbolselection") {
            SymbolSelectionScreen()
        }
        composable("tts") {
            TextToSpeechScreen()
        }
        composable("createsymbol") {
            SymbolCreationScreen()
        }
        composable("settings") {
            SettingsScreen()
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(item) },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = null
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    SpeechBuddyTheme {
        HomeScreen()
    }
}