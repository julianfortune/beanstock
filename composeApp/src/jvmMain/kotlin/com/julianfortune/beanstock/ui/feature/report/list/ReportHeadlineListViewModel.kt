package com.julianfortune.beanstock.ui.feature.report.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.beanstock.data.repository.ReportRepository
import com.julianfortune.beanstock.ui.coordinator.report.ReportViewCoordinator
import com.julianfortune.beanstock.ui.coordinator.report.data.ReportViewState
import com.julianfortune.beanstock.ui.feature.report.list.data.CreateReportBody
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReportHeadlineListViewModel(
    private val reportRepository: ReportRepository,
    private val reportViewCoordinator: ReportViewCoordinator,
) : ViewModel() {

    val selectedId = reportViewCoordinator.state.map {
        when (it) {
            ReportViewState.Empty -> null
            ReportViewState.Loading -> null
            is ReportViewState.Viewing -> it.currentReport.id
        }
    }

    val allReports = reportRepository.getAllAsHeadlines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun onSelect(id: Long) {
        reportViewCoordinator.view(id)
    }

    fun createNewReport(body: CreateReportBody) {
        viewModelScope.launch {
            val result = reportRepository.insert(
                body.name,
                body.start,
                body.end,
                null,
                null,
                null,
                null,
                null,
                null,
            )

            result.map { newId ->
                reportViewCoordinator.view(newId)
            }
        }
    }
}
