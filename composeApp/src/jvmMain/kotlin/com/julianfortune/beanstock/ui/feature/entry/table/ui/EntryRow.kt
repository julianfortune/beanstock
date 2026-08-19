package com.julianfortune.beanstock.ui.feature.entry.table.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.julianfortune.beanstock.ui.theme.AppPreview

@Composable
fun EntryRow(
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    modifier: Modifier = Modifier,
    content: @Composable EntryRowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = verticalAlignment,
    ) {
        val scope = EntryRowScopeImpl(this)
        scope.content()
    }
}

@Preview
@Composable
fun EntryRowPreview() {
    AppPreview {
        EntryRow {
            ItemNameCell { Text("Item Name") }
            ProgramCell { Text("Program Name") }
            PurchasingAccountCell { Text("Account") }
            UnitCountCell { Text("4", fontFamily = FontFamily.Monospace) }
            EntryWeightCell { Text("4", fontFamily = FontFamily.Monospace) }
            EntryCostCell { Text("4", fontFamily = FontFamily.Monospace) }
            ActionCell { Icon(Icons.Default.MoreVert, contentDescription = "Options") }
        }
    }
}

@Preview
@Composable
fun EntryRowSelectablePreview() {
    AppPreview {
        EntryRow {
            SelectionCell(true) { Checkbox(checked = false, onCheckedChange = {}) }
            ItemNameCell { Text("Item Name") }
            ProgramCell { Text("Program Name") }
            PurchasingAccountCell { Text("Account") }
            UnitCountCell { Text("4", fontFamily = FontFamily.Monospace) }
            EntryWeightCell { Text("4", fontFamily = FontFamily.Monospace) }
            EntryCostCell { Text("4", fontFamily = FontFamily.Monospace) }
            ActionCell { Icon(Icons.Default.MoreVert, contentDescription = "Options") }
        }
    }
}
