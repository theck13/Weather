package com.heckofanapp.weather.core.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
fun TopBarScaffold(
    title: String,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    fab: @Composable () -> Unit = {},
    bottomBar: @Composable (() -> Unit) = {},
    defaultCollapsed: Boolean = false,
    content: @Composable (PaddingValues) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val topAppBarState = rememberTopAppBarState()

    LaunchedEffect(defaultCollapsed) {
        if (defaultCollapsed) {
            topAppBarState.heightOffset = topAppBarState.heightOffsetLimit
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(
            connection = scrollBehavior.nestedScrollConnection,
        ),
        bottomBar = bottomBar,
        containerColor = MaterialTheme.colorScheme.background,
        content = content,
        floatingActionButton = fab,
        topBar = {
            val overlappedFraction by remember {
                derivedStateOf { scrollBehavior.state.overlappedFraction }
            }
            val shadowElevation by animateDpAsState(
                label = "Top Bar Shadow",
                targetValue = 4.dp * overlappedFraction,
            )
            Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = shadowElevation,
            ) {
                TopAppBar(
                    actions = actions,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                    navigationIcon = navigationIcon,
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = title,
                        )
                    },
                )
            }
        },
    )
}
