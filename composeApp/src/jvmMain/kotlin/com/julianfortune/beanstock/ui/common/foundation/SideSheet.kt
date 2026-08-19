package com.julianfortune.beanstock.ui.common.foundation

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties


/**
 * SideSheet for displaying static content
 */
@Composable
fun SideSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit = {},
    shape: Shape = RectangleShape,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(color),
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    weight: Float? = null,
    scrimColor: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
    leadingSpaceWidth: Dp? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val visibleState = remember { MutableTransitionState(isVisible) }

    LaunchedEffect(isVisible) {
        visibleState.targetState = isVisible
    }

    SideSheetUi(
        visibleState,
        onDismissRequest = onDismissRequest,
        shape = shape,
        color = color,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border,
        weight = weight,
        scrimColor = scrimColor,
        leadingSpaceWidth = leadingSpaceWidth,
        modifier = modifier,
    ) {
        content()
    }
}


/**
 * SideSheet for displaying dynamic content based on `state`
 */
@Composable
fun <State> SideSheet(
    state: State?,
    onDismissRequest: () -> Unit = {},
    shape: Shape = RectangleShape,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(color),
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    weight: Float? = null,
    scrimColor: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
    leadingSpaceWidth: Dp? = null,
    modifier: Modifier = Modifier,
    content: @Composable (currentState: State) -> Unit,
) {
    val cachedState = remember { mutableStateOf(state) }
    val visibleState = remember { MutableTransitionState(state != null) }

    LaunchedEffect(state) {
        when (state) {
            // Initiate close animation but retain the cached value to preserve `content` during animation out
            null -> visibleState.targetState = false
            // Initiate the opening animation and cache the state value
            else -> {
                cachedState.value = state
                visibleState.targetState = true
            }
        }
    }

    SideSheetUi(
        visibleState,
        onDismissRequest = onDismissRequest,
        shape = shape,
        color = color,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border,
        weight = weight,
        scrimColor = scrimColor,
        leadingSpaceWidth = leadingSpaceWidth,
        modifier = modifier,
    ) {
        cachedState.value?.let { currentState ->
            content(currentState)
        }
    }
}

@Composable
fun SideSheetUi(
    visibleState: MutableTransitionState<Boolean> = remember { MutableTransitionState(true) },
    onDismissRequest: () -> Unit = {},
    shape: Shape = RectangleShape,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(color),
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    weight: Float? = null,
    scrimColor: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
    leadingSpaceWidth: Dp? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val isTransitioning = visibleState.currentState != visibleState.targetState
    if (isTransitioning || visibleState.currentState) {
        Popup(
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(
                focusable = true, // Allow 'esc' to close
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                // Darkening backdrop
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(scrimColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onDismissRequest()
                            }
                    )
                }

                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = slideIn(initialOffset = { IntOffset(x = it.width, y = 0) }),
                    exit = slideOut { IntOffset(x = it.width, y = 0) },
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        val spacerModifier = when (leadingSpaceWidth) {
                            null -> Modifier.weight(1f, true)
                            else -> Modifier.width(leadingSpaceWidth)
                        }

                        Spacer(spacerModifier)

                        val surfaceModifier = when (weight) {
                            null -> modifier.fillMaxHeight()
                            else -> modifier.fillMaxHeight().weight(weight)
                        }

                        Surface(
                            modifier = surfaceModifier,
                            shape = shape,
                            color = color,
                            contentColor = contentColor,
                            tonalElevation = tonalElevation,
                            shadowElevation = shadowElevation,
                            border = border,
                        ) {
                            content()
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun SideSheetPreview() {
    MaterialTheme {
        SideSheetUi(
            weight = 1f
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Hello, world!")
            }
        }
    }
}
