package com.julianfortune.beanstock.ui.common.foundation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dialog(
    onDismissRequest: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            shape = MaterialTheme.shapes.large,
        ) {
            content()
        }
    }
}