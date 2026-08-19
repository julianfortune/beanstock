package com.julianfortune.beanstock.ui.feature.entry.table.data

data class EntryTableState(
    val rows: List<EntryRowState>,
    val selection: SelectionState,
    val action: EntryAction?,
    val totalCount: String,
    val totalWeight: String,
    val totalCost: String,
) {
    companion object {
        fun empty() = EntryTableState(
            emptyList(),
            SelectionState.Disabled,
            null,
            "0",
            "0.0",
            "$0.00"
        )

    }
}
