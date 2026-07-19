package com.pranshulgg.weather_master_app.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import com.pranshulgg.weather_master_app.widgets.card.ui.WidgetCardPreview
import com.pranshulgg.weather_master_app.widgets.daily.ui.WidgetDailyPreview
import com.pranshulgg.weather_master_app.widgets.hourly.ui.WidgetHourlyPreview
import com.pranshulgg.weather_master_app.widgets.model.WidgetVariant
import com.pranshulgg.weather_master_app.widgets.transparent.ui.WidgetTransparentPreview
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetThemeText

@Preview
@Composable
fun PreviewWidgetCard() {
    Column(
        modifier = Modifier.padding(
            all = 16.dp,
        ),
    ) {
        WidgetCardPreview(
            fontSize = 1.00f,
            showBackground = true,
            variant = WidgetVariant.LARGE,
        )
    }
}

@Preview
@Composable
fun PreviewWidgetDaily() {
    Column(
        modifier = Modifier.padding(
            all = 16.dp,
        ),
    ) {
        WidgetDailyPreview(
            dailyCount = 5.00f,
            fontSize = 1.00f,
            format = "EEE d MMM",
            iconSize = 1.00f,
            textTheme = WidgetThemeText.SYSTEM,
            widgetTheme = WidgetTheme.SYSTEM,
        )
    }
}

@Preview
@Composable
fun PreviewWidgetHourly() {
    Column(
        modifier = Modifier.padding(
            all = 16.dp,
        ),
    ) {
        WidgetHourlyPreview(
            fontSize = 1.25f,
            hourlyCount = 8.00f,
            iconSize = 1.25f,
            variant = WidgetVariant.LARGE,
        )
    }
}

@Preview
@Composable
fun PreviewWidgetPill() {
    Column(
        modifier = Modifier
            .padding(
                all = 16.dp,
            )
            .size(
                size = 192.dp,
            ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.background,
                ),
                contentDescription = null,
                painter = painterResource(R.drawable.il_pill_shape),
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier.padding(
                        start = 36.dp,
                    ),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 72.sp,
                    ),
                    text = "37°",
                )

                Spacer(
                    modifier = Modifier.height(
                        height = 50.dp,
                    ),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(
                        start = 16.dp,
                        top = 64.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier.size(
                        size = 96.dp,
                    ),
                    contentDescription = null,
                    painter = painterResource(R.drawable.il_weather_clear_day),
                )

                Spacer(
                    modifier = Modifier.width(
                        60.dp,
                    ),
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewWidgetSummary() {
    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(
                    size = ShapeRadius.Large,
                ),
            )
            .padding(
                all = 16.dp,
            ),
    ) {
        Row {
            Image(
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.secondary,
                ),
                contentDescription = null,
                painter = painterResource(R.drawable.ic_article_24),
            )

            Spacer(
                modifier = Modifier.width(
                    width = 5.dp,
                ),
            )

            Text(
                style = TextStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                ),
                text = stringResource(R.string.setting_day_summary),
            )
        }

        Spacer(
            modifier = Modifier.height(
                height = 5.dp,
            ),
        )

        Text(
            style = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            ),
            text = "Mostly Clear with clouds through most of the day.  Daytime temperatures will average near 37°.  Moderate UV exposure is expected.",
        )
    }
}

@Preview
@Composable
fun PreviewWidgetTransparent() {
    Column(
        modifier = Modifier.padding(
            all = 16.dp,
        ),
    ) {
        WidgetTransparentPreview(
            clockSize = 36.00f,
            format = "EEE d MMM",
            showClock = true,
        )
    }
}
