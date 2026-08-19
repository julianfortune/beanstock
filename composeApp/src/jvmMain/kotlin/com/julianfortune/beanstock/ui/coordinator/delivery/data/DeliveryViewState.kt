package com.julianfortune.beanstock.ui.coordinator.delivery.data

import com.julianfortune.beanstock.data.model.Delivery

sealed interface DeliveryViewState {
    data object Empty : DeliveryViewState
    data object Loading : DeliveryViewState
    data class Viewing(
        val currentDelivery: Delivery,
    ) : DeliveryViewState
}
