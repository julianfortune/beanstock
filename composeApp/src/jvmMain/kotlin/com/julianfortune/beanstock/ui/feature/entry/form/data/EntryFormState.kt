package com.julianfortune.beanstock.ui.feature.entry.form.data

import com.julianfortune.beanstock.ui.common.data.Dynamic
import com.julianfortune.beanstock.ui.common.data.Option


data class EntryFormState(
    val title: String = "New Entry",
    val submissionText: String = "Create",
    val selectedItem: Dynamic<Option<Long>?> = Dynamic.Present(null),
    val itemWeightOptions: ItemWeightOptionsState = ItemWeightOptionsState.Disabled,
    val selectedItemWeightIndex: Int? = null,
    val unitWeight: UnitWeightState = UnitWeightState.LooseItems(),
    val costStatusIsNoCost: Boolean = false,
    val unitCost: String = "",
    val unitCount: String = "",
    val selectedProgramId: Long? = null,
    val selectedAccountId: Long? = null,
    val isValid: Boolean = false,
)


sealed interface ItemWeightOptionsState {
    data object Loading : ItemWeightOptionsState
    data object Disabled : ItemWeightOptionsState
    data class Enabled(
        val options: List<Option<Int>> = emptyList(),
    ) : ItemWeightOptionsState
}

sealed interface UnitWeightState {
    data class LooseItems(
        val unitWeightPounds: String = "",
        val unitWeightOunces: String = "",
    ) : UnitWeightState

    data class PackagedItems(val itemCount: String = "") : UnitWeightState
}
