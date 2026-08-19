package com.julianfortune.beanstock.ui.feature.delivery.form

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.julianfortune.beanstock.ui.common.data.FormFieldState
import com.julianfortune.beanstock.ui.common.input.CurrencyInput
import com.julianfortune.beanstock.ui.common.input.LocalDateInput
import com.julianfortune.beanstock.ui.feature.delivery.form.data.DeliveryBody
import com.julianfortune.beanstock.ui.feature.delivery.form.data.DeliveryFormState


class DeliveryFormStateHolder(initialValues: DeliveryBody? = null) {

    private var receivedDateInput by mutableStateOf<LocalDateInput?>(
        initialValues?.let { LocalDateInput.of(it.received) } ?: LocalDateInput.ofToday()
    )
    private var supplierId by mutableStateOf(initialValues?.supplierId)
    private var fees by mutableStateOf(
        initialValues?.let { CurrencyInput.fromLong(it.feesCents ?: 0) }
    )
    private var taxes by mutableStateOf(
        initialValues?.let { CurrencyInput.fromLong(it.taxesCents ?: 0) }
    )

    val validData: DeliveryBody? by derivedStateOf {
        val currentContextDate = receivedDateInput
        val currentSupplierId = supplierId

        if (currentContextDate is LocalDateInput.Valid && currentSupplierId != null) {
            DeliveryBody(
                received = currentContextDate.parsed,
                supplierId = currentSupplierId,
                taxesCents = taxes?.toLong(),
                feesCents = fees?.toLong(),
            )
        } else {
            null
        }
    }

    val uiState: DeliveryFormState by derivedStateOf {
        DeliveryFormState(
            receivedDate = FormFieldState(
                value = receivedDateInput?.value ?: "",
                isError = receivedDateInput !is LocalDateInput.Valid
            ),
            selectedSupplierId = FormFieldState(value = supplierId),
            taxes = FormFieldState(value = taxes?.value ?: ""),
            fees = FormFieldState(value = fees?.value ?: ""),
            isValid = validData != null,
        )
    }

    fun onReceivedDateChange(value: LocalDateInput?) {
        receivedDateInput = value
    }

    fun onSupplierIdChange(value: Long?) {
        supplierId = value
    }

    fun onFeesChange(value: CurrencyInput) {
        fees = value
    }

    fun onTaxesChange(value: CurrencyInput) {
        taxes = value
    }
}
