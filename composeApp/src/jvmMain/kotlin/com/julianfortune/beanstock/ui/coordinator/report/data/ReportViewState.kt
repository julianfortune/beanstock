package com.julianfortune.beanstock.ui.coordinator.report.data

import com.julianfortune.beanstock.data.model.Report

sealed interface ReportViewState {
    data object Empty : ReportViewState
    data object Loading : ReportViewState
    data class Viewing(
        val currentReport: Report,
    ) : ReportViewState
}
