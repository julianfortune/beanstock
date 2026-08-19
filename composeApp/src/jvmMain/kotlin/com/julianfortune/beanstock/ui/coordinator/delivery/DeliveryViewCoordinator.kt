package com.julianfortune.beanstock.ui.coordinator.delivery

import com.julianfortune.beanstock.ui.coordinator.delivery.data.DeliveryViewState
import kotlinx.coroutines.flow.StateFlow

interface DeliveryViewCoordinator {
    val state: StateFlow<DeliveryViewState>

    fun view(deliveryId: Long)

    fun clear()
}
