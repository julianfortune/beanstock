package com.julianfortune.beanstock.ui.feature.delivery.form.data

import com.julianfortune.beanstock.ui.common.data.FormFieldState
import com.julianfortune.beanstock.ui.common.input.LocalDateInput

data class DeliveryFormState(
    val receivedDate: FormFieldState<String> = FormFieldState(LocalDateInput.ofToday().value),
    val selectedSupplierId: FormFieldState<Long?> = FormFieldState(null),
    val taxes: FormFieldState<String> = FormFieldState(""),
    val fees: FormFieldState<String> = FormFieldState(""),
    val isValid: Boolean = false,
)
