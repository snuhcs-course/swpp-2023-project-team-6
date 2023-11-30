package com.example.speechbuddy.compose

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
import com.example.speechbuddy.compose.settings.GuideScreen
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeechBuddyHome(
    initialPage: Boolean
) {
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
            R.string.create_new_symbol,
            R.drawable.outline_add_a_photo_24
        ),
        BottomNavItem(
            "settings",
            R.string.settings,
            R.drawable.outline_settings_24
        )
    )

    val topAppBarState = rememberSaveable { mutableStateOf(true) }
    val bottomNavBarState = rememberSaveable { mutableStateOf(true) }

    val showGuide = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                topAppBarState = topAppBarState,
                items = navItems,
                navController = navController,
                showGuide = showGuide
            )
        },
        bottomBar = {
            BottomNavigationBar(
                bottomNavBarState = bottomNavBarState,
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
            paddingValues = paddingValues,
            initialPage = initialPage,
            topAppBarState = topAppBarState,
            bottomNavBarState = bottomNavBarState
        )
    }

    if (showGuide.value) {
        GuideScreen(
            onDismissRequest = { showGuide.value = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    topAppBarState: MutableState<Boolean>,
    items: List<BottomNavItem>,
    navController: NavController,
    showGuide: MutableState<Boolean>
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    var titleResId: Int? = null
    items.forEach { item ->
        if (item.route == backStackEntry.value?.destination?.route) titleResId = item.nameResId
    }

    AnimatedVisibility(
        visible = topAppBarState.value,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        CenterAlignedTopAppBar(
            title = {
                if (titleResId != null) { // ensure that titleResId is properly initialized
                    Text(
                        text = stringResource(id = titleResId!!),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            navigationIcon = {
                Image(
                    painter = painterResource(id = R.drawable.speechbuddy_parrot),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier
                        .padding(6.dp)
                        .size(40.dp),
                    contentScale = ContentScale.Fit
                )
            },
            actions = {
                IconButton(onClick = { showGuide.value = true }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "guide"
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
    }
}

@Composable
private fun BottomNavigationBar(
    bottomNavBarState: MutableState<Boolean>,
    items: List<BottomNavItem>,
    navController: NavController,
    onItemClick: (BottomNavItem) -> Unit
) {
    AnimatedVisibility(
        visible = bottomNavBarState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        val backStackEntry = navController.currentBackStackEntryAsState()

        NavigationBar(
            modifier = Modifier.height(64.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            windowInsets = WindowInsets(top = 16.dp)
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
                    modifier = Modifier.size(32.dp),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                            alpha = .5f
                        )
                    ),
                    interactionSource = NoRippleInteractionSource()
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SpeechBuddyHomeNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    initialPage: Boolean,
    topAppBarState: MutableState<Boolean>,
    bottomNavBarState: MutableState<Boolean>
) {
    val startDestination =
        if (initialPage) {
            "symbol_selection"
        } else {
            "text_to_speech"
        }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("symbol_selection") {
            SymbolSelectionScreen(
                paddingValues = paddingValues,
                topAppBarState = topAppBarState,
                bottomNavBarState = bottomNavBarState
            )
        }
        composable("text_to_speech") {
            TextToSpeechScreen(
                paddingValues = paddingValues
            )
        }
        composable("symbol_creation") {
            SymbolCreationScreen(
                paddingValues = paddingValues
            )
        }
        composable("settings") {
            SettingsScreen(
                paddingValues = paddingValues
            )
        }
    }
}