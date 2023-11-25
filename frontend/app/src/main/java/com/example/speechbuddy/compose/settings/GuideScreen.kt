package com.example.speechbuddy.compose.settings

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GuideScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    bottomPaddingValues: PaddingValues
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        PagerIndicatorSample()


//        val pagerState = rememberPagerState(pageCount = {
//            4
//        })
//        HorizontalPager(
//            state = pagerState,
//            modifier = Modifier.fillMaxSize()
//        ) { page ->
//            // Our page content
//            Text(
//                text = "Page: $page",
//            )
//        }
//        Row(
//            Modifier
//                .wrapContentHeight()
//                .fillMaxWidth()
//                .padding(bottom = 8.dp),
//            horizontalArrangement = Arrangement.Center
//        ) {
//            repeat(pagerState.pageCount) { iteration ->
//                val color =
//                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
//                Box(
//                    modifier = Modifier
//                        .padding(2.dp)
//                        .clip(CircleShape)
//                        .background(color)
//                        .size(16.dp)
//                )
//            }
//        }
//        Scaffold(
//            topBar = {
//                TopAppBarUi(
//                    title = stringResource(id = R.string.settings),
//                    onBackClick = onBackClick,
//                    isBackClickEnabled = true
//                )
//            }
//        ) { topPaddingValues ->
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(
//                        top = topPaddingValues.calculateTopPadding(),
//                        bottom = bottomPaddingValues.calculateBottomPadding()
//                    )
//                    .padding(24.dp)
//                    .verticalScroll(rememberScrollState()),
//                verticalArrangement = Arrangement.Center
//            ) {
//                TitleUi(title = stringResource(id = R.string.display_settings))
//
//                Spacer(modifier = modifier.height(20.dp))
//
//                Text("Item 1")
//                Text("Item 2")
//                Text("Item 3")
//                Text("Item 4")
//                Text("Item 5")
//                Text("Item 6")
//                Text("Item 7")
//                Text("Item 8")
//                Text("Item 9")
//                Text("Item 10")
//            }
//        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PagerIndicatorSample() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(40.dp))
        val pagerState1 = rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f
        ) {
            5
        }
        val coroutineScope = rememberCoroutineScope()

        HorizontalPager(
            state = pagerState1,
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .shadow(1.dp, RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Text $it",
                    fontSize = 40.sp,
                    color = Color.Gray
                )
            }
        }

        PagerIndicator(pagerState = pagerState1) {
            coroutineScope.launch {
                pagerState1.scrollToPage(it)
            }
        }

//        val pagerState2 = rememberPagerState(
//            initialPage = 0,
//            initialPageOffsetFraction = 0f
//        ) {
//            10
//        }
//
//        PagerIndicator(
//            pagerState = pagerState2,
//            indicatorSize = 24.dp,
//            indicatorCount = 7,
//            activeColor = Color(0xffFFC107),
//            inActiveColor = Color(0xffFFECB3),
//            indicatorShape = CutCornerShape(10.dp)
//        )
//        HorizontalPager(
//            //count = 10,
//            state = pagerState2,
//        ) {
//            Box(
//                modifier = Modifier
//                    .padding(10.dp)
//                    .shadow(1.dp, RoundedCornerShape(8.dp))
//                    .background(Color.White)
//                    .fillMaxWidth()
//                    .height(200.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    "Text $it",
//                    fontSize = 40.sp,
//                    color = Color.Gray
//                )
//            }
//        }

//        Row(
//            modifier = Modifier.fillMaxSize(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            val pagerState3 = rememberPagerState(
//                initialPage = 0,
//                initialPageOffsetFraction = 0f
//            ) {
//                10
//            }
//
//            Spacer(modifier = Modifier.width(10.dp))

//            PagerIndicator(
//                pagerState = pagerState3,
//                orientation = IndicatorOrientation.Vertical
//            )

//            Spacer(modifier = Modifier.width(20.dp))
//            VerticalPager(
//                //count = 10,
//                state = pagerState3,
//            ) {
//                Box(
//                    modifier = Modifier
//                        .padding(10.dp)
//                        .shadow(1.dp, RoundedCornerShape(8.dp))
//                        .background(Color.White)
//                        .fillMaxWidth()
//                        .height(200.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        "Text $it",
//                        fontSize = 40.sp,
//                        color = Color.Gray
//                    )
//                }
//            }
//        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    indicatorCount: Int = 5,
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
//            indicatorCount,
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
//    indicatorCount: Int,
    indicatorShape: Shape,
    activeColor: Color,
    inActiveColor: Color,
    indicatorSize: Dp,
    onClick: ((Int) -> Unit)?
) {
    items(itemCount) { index ->

        val isSelected = (index == currentItem)

        // Index of item in center when odd number of indicators are set
        // for 5 indicators this is 2nd indicator place
//        val centerItemIndex = indicatorCount / 2

//        val right1 =
//            (currentItem < centerItemIndex &&
//                    index >= indicatorCount - 1)
//
//        val right2 =
//            (currentItem >= centerItemIndex &&
//                    index >= currentItem + centerItemIndex &&
//                    index < itemCount - centerItemIndex + 1)
//        val isRightEdgeItem = right1 || right2

        // Check if this item's distance to center item is smaller than half size of
        // the indicator count when current indicator at the center or
        // when we reach the end of list. End of the list only one item is on edge
        // with 10 items and 7 indicators
        // 7-3= 4th item can be the first valid left edge item and
//        val isLeftEdgeItem =
//            index <= currentItem - centerItemIndex &&
//                    currentItem > centerItemIndex &&
//                    index < itemCount - indicatorCount + 1

        Box(
            modifier = Modifier
                .graphicsLayer {
//                    val scale = if (isSelected) {
//                        1f
//                    } else if (isLeftEdgeItem || isRightEdgeItem) {
//                        .5f
//                    } else {
//                        .8f
//                    }
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