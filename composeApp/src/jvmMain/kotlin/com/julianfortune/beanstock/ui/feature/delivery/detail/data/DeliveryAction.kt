package com.julianfortune.beanstock.ui.feature.delivery.detail.data

import com.julianfortune.beanstock.ui.feature.delivery.form.data.DeliveryBody

sealed interface DeliveryAction {
    data class Edit(val id: Long, val delivery: DeliveryBody) : DeliveryAction
    data class Delete(val id: Long) : DeliveryAction
}
