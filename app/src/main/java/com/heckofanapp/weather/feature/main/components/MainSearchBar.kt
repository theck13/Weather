package com.heckofanapp.weather.feature.main.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.Symbol
import com.heckofanapp.weather.core.ui.components.Tooltip
import com.heckofanapp.weather.core.ui.navigation.NavigationRoutes
import com.heckofanapp.weather.core.ui.theme.ShadowElevation
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
)
@Composable
fun MainSearchBar(
    isFroggyLayout: Boolean = true,
    paddingValues: PaddingValues,
    navController: NavController,
    drawerState: DrawerState,
    activeLocation: Location?,
    isTabletLike: Boolean,
    scrollState: ScrollState? = null,
) {
    val locationText = activeLocation?.name ?: "••••"
    val scope = rememberCoroutineScope()
    val showDrawer = {
        scope.launch {
            drawerState.apply {
                if (isClosed) open() else close()
            }
        }
    }

    val colors = TopAppBarDefaults.topAppBarColors()
    val colorTransitionFraction by remember(scrollState) {
        derivedStateOf { if ((scrollState?.value ?: 0) > 0) 1.00f else 0.00f }
    }
    val scrollFraction by remember(scrollState) {
        derivedStateOf { ((scrollState?.value ?: 0) / 100f).coerceIn(0.00f, 1.00f) }
    }
    val shadowElevation by animateDpAsState(
        label = "Search Bar Shadow",
        targetValue = ShadowElevation.level2 * scrollFraction,
    )
    val backgroundColor by animateColorAsState(
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "Search Bar Background",
        targetValue =
            if (scrollState != null) {
                lerp(
                    MaterialTheme.colorScheme.background,
                    colors.scrolledContainerColor,
                    FastOutLinearInEasing.transform(colorTransitionFraction),
                )
            } else {
                Color.Transparent
            },
    )

    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
            )
            .fillMaxWidth(),
    ) {
        Surface(
            modifier = Modifier
                .clickable(
                    enabled = isFroggyLayout,
                    onClick = {
                        showDrawer()
                    },
                )
                .then(
                    if (isTabletLike) {
                        Modifier.windowInsetsPadding(
                            insets = WindowInsets.systemBars.union(
                                insets = WindowInsets.displayCutout,
                            ).only(
                                sides = WindowInsetsSides.Right,
                            )
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(
                    top = paddingValues.calculateTopPadding(),
                )
                .drawWithContent {
                    clipRect(
                        bottom = size.height + 16.dp.toPx(),
                    ) {
                        this@drawWithContent.drawContent()
                    }
                },
            color = backgroundColor,
            shadowElevation = shadowElevation,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        height = 64.dp,
                    )
                    .padding(
                        horizontal = 4.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isTabletLike.not()) {
                    Tooltip(
                        preferredPosition = TooltipAnchorPosition.Below,
                        spacing = 10.dp,
                        tooltipText = stringResource(R.string.show_list),
                    ) {
                        IconButton(
                            onClick = {
                                showDrawer()
                            },
                            shapes = IconButtonDefaults.shapes(),
                        ) {
                            Symbol(
                                color = if (isFroggyLayout) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                icon = R.drawable.ic_menu_24,
                            )
                        }
                    }
                }

                Gap(
                    horizontal = 4.dp,
                )

                Text(
                    modifier = Modifier
                        .weight(
                            weight = 1.00f,
                        )
                        .then(
                            if (isTabletLike) {
                                Modifier.padding(
                                    start = 10.dp,
                                )
                            } else {
                                Modifier
                            }
                        ),
                    color = if (isFroggyLayout) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                    text = locationText,
                )

                Gap(
                    horizontal = 4.dp,
                )

                if (isTabletLike) {
                    Tooltip(
                        preferredPosition = TooltipAnchorPosition.Below,
                        spacing = 10.dp,
                        tooltipText = stringResource(R.string.search),
                    ) {
                        IconButton(
                            onClick = {
                                navController.navigate(NavigationRoutes.SEARCH)
                            },
                            shapes = IconButtonDefaults.shapes(),
                        ) {
                            Symbol(
                                color =
                                    if (isFroggyLayout) {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                                icon = R.drawable.ic_search_24,
                            )
                        }
                    }
                }

                Tooltip(
                    preferredPosition = TooltipAnchorPosition.Below,
                    spacing = 10.dp,
                    tooltipText = "Settings",
                ) {
                    IconButton(
                        onClick = {
                            navController.navigate(NavigationRoutes.SETTINGS)
                        },
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        Symbol(
                            color =
                                if (isFroggyLayout) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                            icon = R.drawable.ic_settings_24,
                        )
                    }
                }
            }
        }
    }
}
