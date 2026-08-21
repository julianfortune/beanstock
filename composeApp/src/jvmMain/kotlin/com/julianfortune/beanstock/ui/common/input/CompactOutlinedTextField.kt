package com.julianfortune.beanstock.ui.common.input

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.theme.AppPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    units: String? = null,
    shape: Shape = CircleShape,
) {
    val state = rememberTextFieldState(value)

    // Synchronize incoming changes
    LaunchedEffect(value) {
        if (state.text.toString() != value) {
            state.setTextAndPlaceCursorAtEnd(value)
        }
    }

    // Trigger callbacks when internal state is updated
    LaunchedEffect(state) {
        snapshotFlow { state.text }.collect { newText ->
            onValueChange(newText.toString())
        }
    }

    OutlinedTextField(
        state = state,
        isError = isError,
        modifier = modifier.height(38.dp),
        shape = shape,
        lineLimits = TextFieldLineLimits.SingleLine,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        suffix = units?.let { { Text(it) } }
    )
}

@Preview
@Composable
fun CompactOutlinedTextFieldPreview() = AppPreview {
    Row { CompactOutlinedTextField(
        "90.0",
        {},
        units = "lbs"
    ) }
}

@Preview
@Composable
fun CompactOutlinedTextFieldErrorPreview() = AppPreview {
    Row { CompactOutlinedTextField(
        "Test",
        {},
        isError = true,
    ) }
}
