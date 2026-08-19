package com.julianfortune.beanstock.ui.feature.item.data

import com.julianfortune.beanstock.ui.common.data.FormFieldState

sealed interface ItemFormatState {
    data object Loose : ItemFormatState
    data class Packaged(val sizes: List<String>) : ItemFormatState
}

data class ItemFormState(
    val name: FormFieldState<String> = FormFieldState(""),
    val categoryId: FormFieldState<Long?> = FormFieldState(null),
    val format: ItemFormatState = ItemFormatState.Loose,
    val isValid: Boolean = false,
)
