package com.heckofanapp.weather.feature.shared.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.R
import com.heckofanapp.weather.SpaceDefault

// Small icon button container is 40dp.  So, half is a full circle.
// Square matches small icon button pressed shape.
private val CornerCircle = 20.dp
private val CornerSquare = 8.dp

enum class RefreshButtonShape {
    CircleToSquare,
    SquareToCircle,
}

@Composable
fun RefreshButton(
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    colorTint: Color = MaterialTheme.colorScheme.onSurface,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    shape: RefreshButtonShape = RefreshButtonShape.CircleToSquare,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val cornerDefault = when (shape) {
        RefreshButtonShape.CircleToSquare -> CornerCircle
        RefreshButtonShape.SquareToCircle -> CornerSquare
    }
    val cornerPressed = when (shape) {
        RefreshButtonShape.CircleToSquare -> CornerSquare
        RefreshButtonShape.SquareToCircle -> CornerCircle
    }
    // Animate corners here rather than in icon button, which
    // restarts its animation whenever the shapes object changes.
    val cornerShape by animateDpAsState(
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "RefreshButtonCorner",
        targetValue = if (isPressed || isRefreshing) cornerPressed else cornerDefault,
    )

    IconButton(
        colors = colors,
        enabled = isRefreshing.not(),
        interactionSource = interactionSource,
        onClick = onRefresh,
        shapes = IconButtonShapes(
            shape = RoundedCornerShape(
                size = cornerShape,
            ),
        ),
    ) {
        // Detect refreshing change from true to false synchronously
        // during composition so icon animates only on refresh done,
        // not on initial load.
        val wasRefreshing = remember { mutableStateOf(isRefreshing) }
        val isRefreshed = wasRefreshing.value && isRefreshing.not()

        SideEffect {
            wasRefreshing.value = isRefreshing
        }

        if (isRefreshing) {
            CircularProgressIndicator(
                modifier = Modifier.size(
                    size = SpaceDefault,
                ),
                color = colorTint,
                strokeWidth = 2.dp,
            )
        } else {
            val iconRefresh = AnimatedImageVector.animatedVectorResource(R.drawable.av_refresh_24)

            // Start un-drawn only when just finished refreshing,
            // then flip to end state so painter animates once.
            var atEnd by remember { mutableStateOf(isRefreshed.not()) }

            LaunchedEffect(Unit) {
                if (isRefreshed) {
                    atEnd = true
                }
            }

            Icon(
                contentDescription = stringResource(R.string.refresh_locations),
                painter = rememberAnimatedVectorPainter(
                    animatedImageVector = iconRefresh,
                    atEnd = atEnd,
                ),
                tint = colorTint,
            )
        }
    }
}
