package com.julianfortune.beanstock.ui.feature.report.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.beanstock.core.util.formatCents
import com.julianfortune.beanstock.data.model.ReportResult
import com.julianfortune.beanstock.data.repository.ReportRepository
import com.julianfortune.beanstock.data.repository.ReportResultRepository
import com.julianfortune.beanstock.ui.common.formatLocalDate
import com.julianfortune.beanstock.ui.coordinator.report.ReportViewCoordinator
import com.julianfortune.beanstock.ui.coordinator.report.data.ReportViewState
import com.julianfortune.beanstock.ui.delegate.*
import com.julianfortune.beanstock.ui.feature.report.detail.data.ReportCriteriaBody
import com.julianfortune.beanstock.ui.feature.report.detail.data.ReportCriteriaState
import com.julianfortune.beanstock.ui.feature.report.detail.data.ReportDetailState
import com.julianfortune.beanstock.ui.feature.report.detail.data.ReportResultState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.format.FormatStyle

class ReportDetailViewModel(
    private val reportRepository: ReportRepository,
    private val reportResultRepository: ReportResultRepository,
    private val reportViewCoordinator: ReportViewCoordinator,
    private val itemOptionsProvider: ItemOptionsProvider,
    private val categoryOptionsProvider: CategoryOptionsProvider,
    private val programOptionsProvider: ProgramOptionsProvider,
    private val accountOptionsProvider: AccountOptionsProvider,
    private val supplierOptionsProvider: SupplierOptionsProvider,
) : ViewModel(),
    ItemOptionsProvider by itemOptionsProvider,
    CategoryOptionsProvider by categoryOptionsProvider,
    ProgramOptionsProvider by programOptionsProvider,
    AccountOptionsProvider by accountOptionsProvider,
    SupplierOptionsProvider by supplierOptionsProvider {

    private val editNameState = MutableStateFlow<String?>(null) // Contains the existing name
    private val editCriteriaState = MutableStateFlow<ReportCriteriaBody?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val results: Flow<ReportResult?> = reportViewCoordinator.state.flatMapLatest { viewerState ->
        when (viewerState) {
            is ReportViewState.Viewing -> {
                val criteria = viewerState.currentReport.criteria
                reportResultRepository.getResultsForBasicReportCriteria(
                    criteria.start,
                    criteria.end,
                    criteria.item?.id,
                    criteria.category?.id,
                    criteria.costStatus,
                    criteria.program?.id,
                    criteria.account?.id,
                    criteria.supplier?.id,
                )
            }

            else -> flowOf(null)
        }
    }

    val uiState: StateFlow<ReportDetailState> =
        combine(
            reportViewCoordinator.state,
            results,
            editCriteriaState,
            editNameState
        ) { viewerState, results, edit, name ->
            when (viewerState) {
                is ReportViewState.Empty -> ReportDetailState.Loading
                is ReportViewState.Loading -> ReportDetailState.Loading
                is ReportViewState.Viewing -> {
                    val report = viewerState.currentReport

                    ReportDetailState.Success(
                        basicReportId = report.id,
                        name = report.name,
                        criteria = ReportCriteriaState(
                            formatLocalDate(report.criteria.start, FormatStyle.SHORT),
                            formatLocalDate(report.criteria.end, FormatStyle.SHORT),
                            report.criteria.item,
                            report.criteria.category,
                            report.criteria.costStatus,
                            report.criteria.program,
                            report.criteria.account,
                            report.criteria.supplier,
                        ),
                        results = results?.let {
                            ReportResultState(
                                results.deliveryCount,
                                results.entryCount,
                                results.totalWeight.toPounds().toString(),
                                "$${formatCents(results.totalCostCents)}",
                                "$${formatCents(results.totalDeliveryTaxesCents)}",
                                "$${formatCents(results.totalDeliveryFeesCents)}",
                            )
                        },
                        editCriteria = edit,
                        editName = name,
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ReportDetailState.Loading
        )

    fun onEditName() {
        when (val current = reportViewCoordinator.state.value) {
            is ReportViewState.Viewing -> {
                editNameState.value = current.currentReport.name
            }

            else -> return
        }
    }

    fun onSubmitEditName(newName: String) {
        when (val current = reportViewCoordinator.state.value) {
            is ReportViewState.Viewing -> {
                viewModelScope.launch {
                    val result = reportRepository.updateName(
                        current.currentReport.id,
                        newName,
                    )

                    // TODO: Error-handling
                    result.getOrThrow()
                    editNameState.value = null
                }
            }

            else -> return
        }
    }

    fun onCancelEditName() {
        editNameState.value = null
    }

    fun onDeleteReport() {
        when (val current = reportViewCoordinator.state.value) {
            is ReportViewState.Viewing -> {
                viewModelScope.launch {
                    val result = reportRepository.delete(current.currentReport.id)

                    // TODO: Error-handling
                    result.getOrThrow()
                }
            }

            else -> return
        }
    }

    fun onEditCriteria() {
        when (val current = reportViewCoordinator.state.value) {
            is ReportViewState.Viewing -> {
                val currentCriteria = current.currentReport.criteria
                editCriteriaState.value = ReportCriteriaBody(
                    currentCriteria.start,
                    currentCriteria.end,
                    currentCriteria.item?.id,
                    currentCriteria.category?.id,
                    currentCriteria.costStatus,
                    currentCriteria.program?.id,
                    currentCriteria.account?.id,
                    currentCriteria.supplier?.id,
                )
            }

            else -> return
        }
    }

    fun onSubmitEditCriteria(newCriteria: ReportCriteriaBody) {
        when (val current = reportViewCoordinator.state.value) {
            is ReportViewState.Viewing -> {
                viewModelScope.launch {
                    val result = reportRepository.update(
                        current.currentReport.id,
                        current.currentReport.name,
                        newCriteria.start,
                        newCriteria.end,
                        newCriteria.itemId,
                        newCriteria.categoryId,
                        newCriteria.costStatus,
                        newCriteria.programId,
                        newCriteria.accountId,
                        newCriteria.supplierId,
                    )

                    // TODO: Error-handling
                    result.getOrThrow()
                    editCriteriaState.value = null
                }
            }

            else -> return
        }

    }

    fun onCancelEditCriteria() {
        editCriteriaState.value = null
    }

}
