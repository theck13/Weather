package com.pranshulgg.weather_master_app.feature.blocks.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScaleLayout(
    aboutText: String,
    paddingValues: PaddingValues,
    scaleItems: @Composable () -> Unit,
    hourlyContent: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
    ) {
        val isLandscape = maxWidth > maxHeight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    state = rememberScrollState(),
                )
                .padding(
                    paddingValues = paddingValues,
                )
                .padding(
                    bottom = 16.dp,
                    top = 2.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(
                space = 14.dp,
            ),
        ) {
            hourlyContent()

            if (isLandscape) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 14.dp,
                    ),
                ) {
                    AboutCard(
                        modifier = Modifier.weight(
                            weight = 1.00f,
                        ),
                    ) {
                        AboutCardText(
                            text = aboutText,
                        )
                    }

                    ScaleCard(
                        modifier = Modifier.weight(
                            weight = 1.00f,
                        ),
                        items = scaleItems,
                    )
                }
            } else {
                AboutCard {
                    AboutCardText(
                        text = aboutText,
                    )
                }

                ScaleCard(
                    items = scaleItems,
                )
            }
        }
    }
}
