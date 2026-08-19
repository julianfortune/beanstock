package com.julianfortune.beanstock.ui.common.foundation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.theme.dynamicScrollbarStyle


@Composable
fun ScrollableColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(scrollState)
        ) {
            content()
        }

        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(2.dp),
            adapter = rememberScrollbarAdapter(scrollState),
            style = dynamicScrollbarStyle(MaterialTheme.colorScheme.onBackground)
        )
    }
}
