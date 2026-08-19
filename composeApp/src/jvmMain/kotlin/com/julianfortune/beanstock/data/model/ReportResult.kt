package com.julianfortune.beanstock.data.model

data class ReportResult(
    val deliveryCount: Int,
    val totalDeliveryFeesCents: Long,
    val totalDeliveryTaxesCents: Long,
    val entryCount: Int,
    val totalWeight: Weight,
    val totalCostCents: Long,
) {
    companion object {
        fun ofZeroes() = ReportResult(
            0,
            0,
            0,
            0,
            Weight(0),
            0,
        )
    }
}
