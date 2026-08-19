package com.julianfortune.beanstock.ui.feature.entry.table.data

data class EntryRowState(
    val entryId: Long,
    val isSelected: Boolean,
    val itemName: String,
    val programName: String?,
    val accountName: String?,
    val unitCount: String,
    val totalWeight: String,
    val totalCost: String,
)