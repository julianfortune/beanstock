package com.julianfortune.beanstock.ui.common.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmDeleteEntityForm(
    id: Long,
    text: String,
    onCancel: () -> Unit,
    onConfirm: (id: Long) -> Unit
) {
    ConfirmDeleteEntityForm(
        text,
        onCancel,
        onConfirm = {
            onConfirm(id)
        }
    )
}

@Composable
fun ConfirmDeleteEntityForm(
    text: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = onCancel
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                colors = ButtonDefaults.filledTonalButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
                onClick = onConfirm,
            ) {
                Text("Delete")
            }
        }
    }
}
