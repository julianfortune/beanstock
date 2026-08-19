package com.julianfortune.beanstock.ui.common.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.theme.AppPreview

@Composable
fun ListDetailScaffold(
    listWidth: Dp,
    listView: @Composable () -> Unit,
    separator: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {

    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = modifier) {
        // Left-hand 'list' of items
        Column(modifier = Modifier.width(listWidth)) {
            listView()
        }

        // Separator between columns
        separator?.invoke()

        // Right-hand 'detail' view
        content()
    }
}

@Preview
@Composable
fun DefaultPreview() {
    AppPreview {
        ListDetailScaffold(
            listWidth = 200.dp,
            listView = {
                Column {
                    ListItem({ Text("Item 1") })
                    ListItem({ Text("Item 2") })
                    ListItem({ Text("Item 3") })
                }
            },
            separator = { VerticalDivider(thickness = 1.dp) },
            content = {
                Text("Detail content")
            },
        )
    }
}
