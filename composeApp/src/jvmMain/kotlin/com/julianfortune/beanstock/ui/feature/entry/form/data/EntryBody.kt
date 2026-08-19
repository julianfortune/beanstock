package com.julianfortune.beanstock.ui.feature.entry.form.data

import com.julianfortune.beanstock.data.model.CostStatus
import com.julianfortune.beanstock.data.model.Weight

data class EntryBody(
    val itemId: Long,
    val unitCount: Long,
    val unitWeight: Weight,
    val costStatus: CostStatus,
    val unitCostCents: Long,
    val itemWeight: Weight?,
    val itemsPerUnit: Long?,
    val programId: Long?,
    val accountId: Long?,
)
