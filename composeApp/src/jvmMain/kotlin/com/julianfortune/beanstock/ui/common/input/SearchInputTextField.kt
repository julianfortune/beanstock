package com.julianfortune.beanstock.ui.common.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SearchTextInputField() {
    Box(
        modifier = Modifier
            .height(32.dp) // Match the buttons
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = MaterialTheme.shapes.extraSmall, // Match the buttons
            )
    ) {
        BasicTextField(
            modifier = Modifier.fillMaxHeight().padding(horizontal = 8.dp),
            value = "",
            onValueChange = { },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Search,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    innerTextField()
                }
            }
        )
    }
}