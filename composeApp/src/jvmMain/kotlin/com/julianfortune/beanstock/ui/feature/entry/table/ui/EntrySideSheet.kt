package com.julianfortune.beanstock.ui.feature.entry.table.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.common.foundation.SideSheet

@Composable
fun <State> EntrySideSheet(
    state: State?,
    onDismissRequest: () -> Unit = {},
    content: @Composable (currentState: State) -> Unit,
) {
    SideSheet(
        state,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.width(640.dp),
    ) { currentState ->
        Column(modifier = Modifier.padding(16.dp)) {
            content(currentState)
        }
    }
}
