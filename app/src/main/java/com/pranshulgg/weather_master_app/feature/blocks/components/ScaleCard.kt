package com.pranshulgg.weather_master_app.feature.blocks.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.TouchMinimum
import com.pranshulgg.weather_master_app.core.ui.components.AvatarIcon
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.feature.shared.components.Header

@Composable
fun ScaleCard(
    modifier: Modifier = Modifier,
    items: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.padding(
            horizontal = 16.dp,
        ),
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 16.dp,
                ),
        ) {
            Header(
                icon = R.drawable.ic_linear_scale_24,
                text = stringResource(R.string.text_scale),
            )

            Gap(
                vertical = 8.dp,
            )

            items()
        }
    }
}

@Composable
fun ScaleCardItem(
    containerColor: Color,
    headline: String,
    icon: Int,
    onClick: () -> Unit,
    trailing: String,
) {
    ListItem(
        modifier = Modifier
            .clickable(
                onClick = onClick,
            )
            .defaultMinSize(
                minHeight = TouchMinimum,
            ),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        headlineContent = {
            Text(
                text = headline,
            )
        },
        leadingContent = {
            AvatarIcon(
                containerColor = containerColor,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                icon = icon,
            )
        },
        trailingContent = {
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = trailing,
            )
        },
    )
}
