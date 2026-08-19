package com.julianfortune.beanstock.ui.feature.report.detail.data

import com.julianfortune.beanstock.data.model.CostStatus
import java.time.LocalDate

data class ReportCriteriaBody(
    val start: LocalDate,
    val end: LocalDate,
    val itemId: Long? = null,
    val categoryId: Long? = null,
    val costStatus: CostStatus? = null,
    val programId: Long? = null,
    val accountId: Long? = null,
    val supplierId: Long? = null,
)
