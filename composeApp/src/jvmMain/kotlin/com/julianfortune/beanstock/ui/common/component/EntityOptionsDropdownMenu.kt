package com.julianfortune.beanstock.ui.common.component

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon

@Composable
fun EntityOptionsDropdownMenu(
    edit: () -> Unit,
    delete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OverflowMenu(modifier = modifier) { dismiss ->
        DropdownMenuItem(
            text = { Text("Edit") },
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            onClick = {
                edit()
                dismiss()
            }
        )
        DropdownMenuItem(
            text = { Text("Delete") },
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            onClick = {
                delete()
                dismiss()
            }
        )
    }
}