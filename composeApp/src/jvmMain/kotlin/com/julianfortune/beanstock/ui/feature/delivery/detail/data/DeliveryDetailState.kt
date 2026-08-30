package com.julianfortune.beanstock.ui.feature.delivery.detail.data

sealed interface DeliveryDetailState {
    object Empty : DeliveryDetailState
    object Loading : DeliveryDetailState
    data class Success(
        val title: String,
        val content: DeliveryContentState,
    ) : DeliveryDetailState
}
