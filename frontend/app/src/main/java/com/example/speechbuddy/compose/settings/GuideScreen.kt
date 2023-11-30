package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.speechbuddy.R
import kotlinx.coroutines.launch

val pages = listOf<@Composable () -> Unit>(
    {
        GuideScreenPage(
            imageId = R.drawable.bottom_navigation_bar_description,
            contentDescriptionId = R.string.guide_screen_bottom_navigation_bar
        )
    },
    {
        GuideScreenPage(
            imageId = R.drawable.symbol_selection_screen_description,
            contentDescriptionId = R.string.guide_screen_symbol_selection_screen
        )
    },
    {
        GuideScreenPage(
            imageId = R.drawable.text_to_speech_screen_description,
            contentDescriptionId = R.string.guide_screen_text_to_speech_screen
        )
    },
    {
        GuideScreenPage(
            imageId = R.drawable.symbol_creation_screen_description,
            contentDescriptionId = R.string.guide_screen_symbol_creation_screen
        )
    }
)
val pageSize: Int = pages.size

@Composable
fun GuideScreenPage(
    imageId: Int,
    contentDescriptionId: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = stringResource(id = contentDescriptionId),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(10f / 19f) // image width-height ratio
        )
    }
}

@Composable
fun GuideScreen(
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false // Allow custom width
        )
    ) {
        GuideScreenPageList()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GuideScreenPageList() {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.85f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val pagerState = rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f
        ) {
            pageSize
        }
        val coroutineScope = rememberCoroutineScope()

        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.85f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                pages[page]()
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        PagerIndicator(pagerState = pagerState) {
            coroutineScope.launch {
                pagerState.scrollToPage(it)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    indicatorCount: Int = pageSize,
    indicatorSize: Dp = 16.dp,
    indicatorShape: Shape = CircleShape,
    space: Dp = 8.dp,
    activeColor: Color = Color(0xffEC407A),
    inActiveColor: Color = Color.LightGray,
    onClick: ((Int) -> Unit)? = null
) {

    val listState = rememberLazyListState()

    val totalWidth: Dp = indicatorSize * indicatorCount + space * (indicatorCount - 1)
    val widthInPx = LocalDensity.current.run { indicatorSize.toPx() }

    val currentItem by remember {
        derivedStateOf {
            pagerState.currentPage
        }
    }

    val itemCount = pagerState.pageCount

    LaunchedEffect(key1 = currentItem) {
        val viewportSize = listState.layoutInfo.viewportSize
        listState.animateScrollToItem(
            currentItem,
            (widthInPx / 2 - viewportSize.width / 2).toInt()
        )
    }

    LazyRow(
        modifier = modifier.width(totalWidth),
        state = listState,
        contentPadding = PaddingValues(vertical = space),
        horizontalArrangement = Arrangement.spacedBy(space),
        userScrollEnabled = false
    ) {
        indicatorItems(
            itemCount,
            currentItem,
            indicatorShape,
            activeColor,
            inActiveColor,
            indicatorSize,
            onClick
        )
    }
}

private fun LazyListScope.indicatorItems(
    itemCount: Int,
    currentItem: Int,
    indicatorShape: Shape,
    activeColor: Color,
    inActiveColor: Color,
    indicatorSize: Dp,
    onClick: ((Int) -> Unit)?
) {
    items(itemCount) { index ->

        val isSelected = (index == currentItem)

        Box(
            modifier = Modifier
                .graphicsLayer {
                    val scale = 1f
                    scaleX = scale
                    scaleY = scale
                }
                .clip(indicatorShape)
                .size(indicatorSize)
                .background(
                    if (isSelected) activeColor else inActiveColor,
                    indicatorShape
                )
                .then(
                    if (onClick != null) {
                        Modifier
                            .clickable {
                                onClick.invoke(index)
                            }
                    } else Modifier
                )
        )
    }
}