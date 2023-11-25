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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.speechbuddy.R
import kotlinx.coroutines.launch

val pages = listOf<@Composable () -> Unit>(
    {
        Image(
            painter = painterResource(id = R.drawable.bottom_navigation_bar_description),
            contentDescription = "Description",
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .fillMaxSize()
        )
    },
    { Text("Page 2 Content", fontSize = 40.sp) },
    { Text("Page 3 Content", fontSize = 40.sp) },
    { Text("Page 4 Content", fontSize = 40.sp) },
    { Text("Page 5 Content", fontSize = 40.sp) },
)
val pageSize: Int = pages.size

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GuideScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    bottomPaddingValues: PaddingValues
) {
    Surface(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {
        PagerIndicatorSample()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PagerIndicatorSample() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f)) // Spacer before the pager
        val pagerState1 = rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f
        ) {
            pageSize
        }
        val coroutineScope = rememberCoroutineScope()

        HorizontalPager(
            state = pagerState1,
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(400.dp),
                contentAlignment = Alignment.Center
            ) {
                pages[page]()
            }
        }

        PagerIndicator(pagerState = pagerState1) {
            coroutineScope.launch {
                pagerState1.scrollToPage(it)
            }
        }
        Spacer(modifier = Modifier.weight(1f)) // Spacer after the pager
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