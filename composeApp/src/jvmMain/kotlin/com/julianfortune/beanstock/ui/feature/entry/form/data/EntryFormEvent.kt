package com.julianfortune.beanstock.ui.feature.entry.form.data

import com.julianfortune.beanstock.ui.common.input.CurrencyInput

sealed interface EntryFormEvent {
    data object AbortForm : EntryFormEvent
    data class AccountSelected(val accountId: Long?) : EntryFormEvent
    data class CostStatusChanged(val isNoCost: Boolean) : EntryFormEvent
    data class ItemCountChanged(val value: String) : EntryFormEvent
    data class ItemSelected(val itemId: Long?) : EntryFormEvent
    data class ItemWeightSelected(val index: Int?) : EntryFormEvent
    data class ProgramSelected(val programId: Long?) : EntryFormEvent
    data object SubmitForm : EntryFormEvent
    data class UnitCostChanged(val value: CurrencyInput?) : EntryFormEvent
    data object UnitCostLostFocus : EntryFormEvent
    data class UnitCountChanged(val value: String) : EntryFormEvent
    data class WeightOuncesChanged(val value: String) : EntryFormEvent
    data class WeightPoundsChanged(val value: String) : EntryFormEvent
}
