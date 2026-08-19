package com.julianfortune.beanstock.ui.feature.delivery.form.data

import java.time.LocalDate

data class DeliveryBody(
    val received: LocalDate,
    val supplierId: Long,
    val taxesCents: Long? = null,
    val feesCents: Long? = null,
)
