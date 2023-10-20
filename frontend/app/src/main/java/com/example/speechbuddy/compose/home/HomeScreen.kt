package com.example.speechbuddy.compose.home

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.speechbuddy.compose.BottomNavItem

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        elevation = 10.dp
    ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            BottomNavigationItem(
                selected = selected,
                onClick = { onItemClick(item) },
                selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedContentColor = MaterialTheme.colorScheme.secondary,
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = ""
                    )
                }
            )
        }
    }
}

//@Composable
//fun TtsScreen() {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(text = "TTS")
//    }
//}


//@Composable
//fun SettingsScreen() {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(text = "Settings")
//    }
//}


//enum class SpeechBuddyPage(
//    @StringRes val titleResId: Int,
//    @DrawableRes val drawableResId: Int
//) {
//    TALK_WITH_SYMBOL(R.string.menu_talk_with_symbol, R.drawable.outline_touch_app_24),
//    TTS(R.string.menu_tts, R.drawable.outline_volume_up_24),
//    ADD_SYMBOL(R.string.menu_add_symbol, R.drawable.outline_add_a_photo_24),
//    SETTINGS(R.string.menu_settings, R.drawable.outline_settings_24)
//}
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
//@Composable
//fun HomeScreen(
//    modifier: Modifier = Modifier,
//    onClick: () -> Unit
//) {
//    val pagerState = rememberPagerState(
//        initialPage = 0,
//        initialPageOffsetFraction = 0f,
//        pageCount = { 4 }
//    )
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
//
//    Scaffold(
//        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
////        topBar = {
////            HomeTopAppBar(
////                pagerState = pagerState,
////                onFilterClick = { viewModel.updateData() },
////                scrollBehavior = scrollBehavior
////            )
////        }
//    ) {
//        HomePagerScreen(
//            pagerState = pagerState,
//            modifier = Modifier.padding(it)
//        )
//    }
//}
//
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun HomePagerScreen(
//    pagerState: PagerState,
//    modifier: Modifier = Modifier,
//    pages: Array<SpeechBuddyPage> = SpeechBuddyPage.values()
//) {
//    Column(modifier) {
//        HorizontalPager(
//            modifier = Modifier.background(MaterialTheme.colorScheme.background),
//            state = pagerState,
//            verticalAlignment = Alignment.Top
//        ) { index ->
//            when (pages[index]) {
//                SpeechBuddyPage.TALK_WITH_SYMBOL -> {
//                    TalkWithSymbolScreen()
//                }
//
//                SpeechBuddyPage.TTS -> {
//                    TtsScreen()
//                }
//
//                SpeechBuddyPage.ADD_SYMBOL -> {
//                    AddSymbolScreen()
//                }
//
//                SpeechBuddyPage.SETTINGS -> {
//                    SettingsScreen()
//                }
//            }
//        }
//        //TabRow: menu navigation at the bottom
//        TabRow(
//            selectedTabIndex = pagerState.currentPage,
//            modifier = Modifier.defaultMinSize(minHeight = 0.dp)
//        ) {
//            pages.forEachIndexed { index, page ->
//                val pageName = page.titleResId
//                val bottomIcon = page.drawableResId
//                Tab(
//                    selected = pagerState.currentPage == index,
//                    onClick = {},
//                    icon = {
//                        Icon(
//                            painter = painterResource(id = bottomIcon),
//                            contentDescription = stringResource(id = pageName),
//                            tint = MaterialTheme.colorScheme.onPrimaryContainer
//                        )
//                    },
//                    //selectedContentColor = MaterialTheme.colorScheme.secondaryContainer,
//                    unselectedContentColor = MaterialTheme.colorScheme.secondary
//                )
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalFoundationApi::class)
//@Preview
//@Composable
//private fun HomeScreenPreview() {
//    SpeechBuddyTheme {
//        HomeScreen (onClick = {})
//    }
//}