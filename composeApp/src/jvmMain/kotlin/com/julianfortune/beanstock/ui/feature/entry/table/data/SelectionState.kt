package com.julianfortune.beanstock.ui.feature.entry.table.data

import androidx.compose.ui.state.ToggleableState

sealed interface SelectionState {
    data object Disabled : SelectionState
    data class Enabled(
        val count: Int,
        val selectAll: ToggleableState,
    ) : SelectionState
}
