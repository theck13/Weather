package com.heckofanapp.weather.feature.blocks.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.heckofanapp.weather.R
import com.heckofanapp.weather.SpaceDefault
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.theme.ShadowElevation
import com.heckofanapp.weather.feature.shared.components.Header

@Composable
fun AboutCard(
    modifier: Modifier = Modifier,
    description: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.padding(
            horizontal = SpaceDefault,
        ),
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = SpaceDefault,
                ),
        ) {
            Header(
                icon = R.drawable.ic_info_24,
                text = stringResource(R.string.about),
            )

            Gap(
                vertical = SpaceDefault / 2,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(
                    space = SpaceDefault / 2,
                ),
            ) {
                description()
            }
        }
    }
}

@Composable
fun AboutCardText(
    text: String,
) {
    Text(
        modifier = Modifier.padding(
            horizontal = SpaceDefault,
        ),
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyLarge,
        text = text,
    )
}
