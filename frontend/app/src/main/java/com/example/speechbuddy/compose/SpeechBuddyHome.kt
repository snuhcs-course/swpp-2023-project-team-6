package com.example.speechbuddy.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.stringResource
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
import com.example.speechbuddy.compose.utils.NoRippleInteractionSource

data class BottomNavItem(
    val route: String,
    val nameResId: Int,
    val iconResId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeechBuddyHome() {
    val navController = rememberNavController()
    val navItems = listOf(
        BottomNavItem(
            "symbol_selection",
            R.string.talk_with_symbols,
            R.drawable.outline_touch_app_24
        ),
        BottomNavItem(
            "text_to_speech",
            R.string.talk_with_speech,
            R.drawable.outline_volume_up_24
        ),
        BottomNavItem(
            "symbol_creation",
            R.string.symbol_creation,
            R.drawable.outline_add_a_photo_24
        ),
        BottomNavItem(
            "settings",
            R.string.settings,
            R.drawable.outline_settings_24
        )
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = navItems,
                navController = navController,
                onItemClick = {
                    navController.navigate(it.route)
                }
            )
        }
    ) { paddingValues ->
        SpeechBuddyHomeNavHost(
            navController = navController,
            bottomPaddingValues = paddingValues
        )
    }
}

@Composable
private fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        windowInsets = WindowInsets(top = 20.dp)
    ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(item) },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = stringResource(id = item.nameResId)
                    )
                },
                modifier = Modifier.size(40.dp),
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.outline
                ),
                interactionSource = NoRippleInteractionSource()
            )
        }
    }
}

@Composable
private fun SpeechBuddyHomeNavHost(
    navController: NavHostController,
    bottomPaddingValues: PaddingValues
) {
    NavHost(navController = navController, startDestination = "symbol_selection") {
        composable("symbol_selection") {
            SymbolSelectionScreen(
                bottomPaddingValues = bottomPaddingValues
            )
        }
        composable("text_to_speech") {
            TextToSpeechScreen(
                bottomPaddingValues = bottomPaddingValues
            )
        }
        composable("symbol_creation") {
            SymbolCreationScreen(
                bottomPaddingValues = bottomPaddingValues
            )
        }
        composable("settings") {
            SettingsScreen(
                bottomPaddingValues = bottomPaddingValues
            )
        }
    }
}