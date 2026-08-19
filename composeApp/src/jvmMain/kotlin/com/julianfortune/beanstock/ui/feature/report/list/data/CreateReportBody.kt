package com.julianfortune.beanstock.ui.feature.report.list.data

import java.time.LocalDate

data class CreateReportBody(
    val name: String,
    val start: LocalDate,
    val end: LocalDate,
)
