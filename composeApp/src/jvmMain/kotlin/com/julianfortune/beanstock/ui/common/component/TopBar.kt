package com.julianfortune.beanstock.ui.common.component


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.theme.AppPreview


@Composable
fun TopBar(
    title: String,
    modifier: Modifier = Modifier,
    actionContent: @Composable RowScope.() -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.height(68.dp).padding(horizontal = 16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SelectionContainer {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = LocalContentColor.current
                )
            }

            actionContent()
        }

        HorizontalDivider(thickness = 1.dp)
    }
}

@Preview
@Composable
fun TopBarPreview() {
    AppPreview {
        TopBar(
            title = "Title",
            actionContent = {
                Row {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.QuestionMark, "Example")
                    }
                }
            }
        )
    }
}
