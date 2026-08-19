package com.julianfortune.glacier.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.julianfortune.glacier.data.codec.CostStatusCodec
import com.julianfortune.glacier.data.codec.LocalDateCodec
import com.julianfortune.glacier.data.model.CostStatus
import com.julianfortune.glacier.data.model.ReportResult
import com.julianfortune.glacier.data.model.Weight
import com.julianfortune.glacier.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class ReportResultRepository(private val database: Database) {

    fun getResultsForBasicReportCriteria(
        startDate: LocalDate,
        endDate: LocalDate,
        itemId: Long? = null,
        itemCategoryId: Long? = null,
        costStatus: CostStatus? = null,
        programId: Long? = null,
        purchasingAccountId: Long? = null,
        supplierId: Long? = null,
    ): Flow<ReportResult?> = database.deliveryEntryQueries.getStatisticsByBasicReportCriteria(
        LocalDateCodec.serialize(startDate),
        LocalDateCodec.serialize(endDate),
        itemId,
        itemCategoryId,
        costStatus?.let { CostStatusCodec.serialize(it) },
        programId,
        purchasingAccountId,
        supplierId,
    )
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { result ->
            result?.let {
                ReportResult(
                    result.deliveryCount.toInt(),
                    result.totalDeliveryFeesCents?.toLong() ?: 0,
                    result.totalDeliveryTaxesCents?.toLong() ?: 0,
                    result.entryCount.toInt(),
                    Weight.ofCentigrams(result.totalWeightCentigrams ?: 0),
                    result.totalCostCents ?: 0,
                )
            }
        }

}
