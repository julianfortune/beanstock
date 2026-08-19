package com.julianfortune.glacier.ui.feature.report.detail.data

import com.julianfortune.glacier.data.model.*

sealed interface ReportDetailState {

    object Loading : ReportDetailState

    data class Success(
        val basicReportId: Long,
        val name: String,
        val criteria: ReportCriteriaState,
        val results: ReportResultState? = null,
        val editName: String? = null,
        val editCriteria: ReportCriteriaBody? = null,
    ) : ReportDetailState

    object Error : ReportDetailState

}

data class ReportCriteriaState(
    val startDate: String,
    val endDate: String,
    val item: ItemHeadline? = null,
    val category: Category? = null,
    val costStatus: CostStatus? = null,
    val program: Program? = null,
    val account: Account? = null,
    val supplier: Supplier? = null,
)

data class ReportResultState(
    val deliveryCount: Int,
    val entryCount: Int,
    val totalWeight: String,
    val subtotal: String,
    val totalTaxes: String,
    val totalFees: String,
)