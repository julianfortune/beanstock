package com.julianfortune.beanstock.ui.feature.entry.table.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


private val spaceBetweenCells = 16.dp
private val iconButtonPadding = 8.dp

sealed interface EntryRowScope {
    @Composable
    fun SelectionCell(isVisible: Boolean, content: @Composable () -> Unit)

    @Composable
    fun ItemNameCell(content: @Composable () -> Unit)

    @Composable
    fun ProgramCell(content: @Composable () -> Unit)

    @Composable
    fun PurchasingAccountCell(content: @Composable () -> Unit)

    @Composable
    fun UnitCountCell(content: @Composable () -> Unit)

    @Composable
    fun EntryWeightCell(content: @Composable () -> Unit)

    @Composable
    fun EntryCostCell(content: @Composable () -> Unit)

    @Composable
    fun ActionCell(content: @Composable () -> Unit)
}


internal class EntryRowScopeImpl(private val rowScope: RowScope) : EntryRowScope {

    private fun getColumnWeight(column: String): Float {
        return when (column) {
            "item" -> 2f
            "program" -> 1.5f
            "account" -> 1.5f
            else -> throw NotImplementedError("Column '$column' does not have a weight")
        }
    }

    private fun getColumnWidth(column: String): Dp {
        return when (column) {
            "count" -> 64.dp
            "weight" -> 96.dp
            "total" -> 112.dp
            else -> throw NotImplementedError("Column '$column' does not have a weight")
        }
    }

    @Composable
    override fun SelectionCell(isVisible: Boolean, content: @Composable () -> Unit) {
        AnimatedVisibility(isVisible, enter = expandHorizontally(), exit = shrinkHorizontally()) {
            Row(
                modifier = Modifier.width(36.dp)
                    .padding(start = iconButtonPadding),
            ) {
                content()
            }
        }
    }

    @Composable
    override fun ItemNameCell(content: @Composable (() -> Unit)) {
        with(rowScope) {
            Row(
                modifier = Modifier
                    .weight(getColumnWeight("item"))
                    .padding(start = spaceBetweenCells),
            ) {
                content()
            }
        }
    }

    @Composable
    override fun ProgramCell(content: @Composable (() -> Unit)) {
        with(rowScope) {
            Row(
                modifier = Modifier
                    .weight(getColumnWeight("program"))
                    .padding(start = spaceBetweenCells)
            ) {
                content()
            }
        }
    }

    @Composable
    override fun PurchasingAccountCell(content: @Composable (() -> Unit)) {
        with(rowScope) {
            Row(
                modifier = Modifier
                    .weight(getColumnWeight("account"))
                    .padding(start = spaceBetweenCells)
            ) {
                content()
            }
        }
    }

    @Composable
    override fun UnitCountCell(content: @Composable (() -> Unit)) {
        Row(
            modifier = Modifier
                .width(getColumnWidth("count")),
            horizontalArrangement = Arrangement.End,
        ) {
            content()
        }
    }

    @Composable
    override fun EntryWeightCell(content: @Composable (() -> Unit)) {
        Row(
            modifier = Modifier
                .width(getColumnWidth("weight")),
            horizontalArrangement = Arrangement.End,
        ) {
            content()
        }
    }

    @Composable
    override fun EntryCostCell(content: @Composable (() -> Unit)) {
        Row(
            modifier = Modifier
                .width(getColumnWidth("total"))
                .padding(end = iconButtonPadding),
            horizontalArrangement = Arrangement.End,
        ) {
            content()
        }
    }

    @Composable
    override fun ActionCell(content: @Composable () -> Unit) {
        Row(
            modifier = Modifier.width(36.dp).padding(end = iconButtonPadding),
            horizontalArrangement = Arrangement.End,
        ) {
            content()
        }
    }

}