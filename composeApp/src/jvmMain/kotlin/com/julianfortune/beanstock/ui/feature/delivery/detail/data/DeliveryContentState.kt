package com.julianfortune.beanstock.ui.feature.delivery.detail.data

data class DeliveryContentState(
    val deliveryId: Long,
    val receivedDate: String,
    val supplierName: String,
    val totalCount: String,
    val totalWeight: String,
    val subtotal: String,
    val fees: String,
    val taxes: String,
    val totalCost: String,
)
