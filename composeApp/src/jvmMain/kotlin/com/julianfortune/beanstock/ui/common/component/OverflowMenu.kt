package com.julianfortune.beanstock.ui.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp

@Composable
fun OverflowMenu(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(onDismiss: () -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val dismiss = { expanded = false }

    Box(modifier = modifier) {
        IconButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .size(28.dp)
                .pointerHoverIcon(PointerIcon.Hand)
        ) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = dismiss
        ) {
            content(dismiss)
        }
    }
}