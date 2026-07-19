package com.pranshulgg.weather_master_app.feature.main.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NavigationDrawer(
    drawerContent: @Composable () -> Unit,
    drawerState: DrawerState,
    isTabletLike: Boolean,
    content: @Composable () -> Unit,
) {
    if (isTabletLike) {
        PermanentNavigationDrawer(
            content = {
                content()
            },
            drawerContent = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(
                            width = 330.dp,
                        ),
                ) {
                    drawerContent()
                }
            }
        )
    } else {
        DismissibleNavigationDrawer(
            content = {
                content()
            },
            drawerContent = drawerContent,
            drawerState = drawerState,
        )
    }
}
