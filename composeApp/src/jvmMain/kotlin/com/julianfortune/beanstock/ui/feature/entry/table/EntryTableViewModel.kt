package com.julianfortune.beanstock.ui.feature.entry.table

import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.beanstock.core.util.formatCents
import com.julianfortune.beanstock.data.repository.DeliveryRepository
import com.julianfortune.beanstock.ui.coordinator.delivery.DeliveryViewCoordinator
import com.julianfortune.beanstock.ui.coordinator.delivery.data.DeliveryViewState
import com.julianfortune.beanstock.ui.delegate.AccountOptionsProvider
import com.julianfortune.beanstock.ui.delegate.ProgramOptionsProvider
import com.julianfortune.beanstock.ui.feature.delivery.detail.calculateDeliverySubTotalCostCents
import com.julianfortune.beanstock.ui.feature.delivery.detail.calculateDeliveryTotalWeightPounds
import com.julianfortune.beanstock.ui.feature.delivery.detail.calculateEntryTotalCostCents
import com.julianfortune.beanstock.ui.feature.delivery.detail.calculateEntryTotalWeight
import com.julianfortune.beanstock.ui.feature.entry.form.data.EntryBody
import com.julianfortune.beanstock.ui.feature.entry.table.data.EntryAction
import com.julianfortune.beanstock.ui.feature.entry.table.data.EntryRowState
import com.julianfortune.beanstock.ui.feature.entry.table.data.EntryTableState
import com.julianfortune.beanstock.ui.feature.entry.table.data.SelectionState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

class EntryTableViewModel(
    private val deliveryViewCoordinator: DeliveryViewCoordinator,
    private val deliveryRepository: DeliveryRepository,
    programOptionsProvider: ProgramOptionsProvider,
    accountOptionsProvider: AccountOptionsProvider,
) : ViewModel(),
    ProgramOptionsProvider by programOptionsProvider,
    AccountOptionsProvider by accountOptionsProvider {

    private val entryAction = MutableStateFlow<EntryAction?>(null)
    private val selectionEnabled = MutableStateFlow(false)
    private val selectedEntryRows = MutableStateFlow<Set<Long>>(emptySet())

    val uiState: StateFlow<EntryTableState?> = combine(
        deliveryViewCoordinator.state,
        entryAction,
        selectionEnabled,
        selectedEntryRows,
    ) { viewerState, entryAction, selectionEnabled, selections ->
        when (viewerState) {
            is DeliveryViewState.Empty, is DeliveryViewState.Loading -> null
            is DeliveryViewState.Viewing -> {
                val delivery = viewerState.currentDelivery

                val selectionState = when {
                    selectionEnabled -> {
                        val selectAll = when (selections.size) {
                            0 -> ToggleableState.Off
                            else -> {
                                val unselectedEntries = delivery.entries.map { it.id }.toSet().minus(selections)
                                when (unselectedEntries.size) {
                                    0 -> ToggleableState.On
                                    else -> ToggleableState.Indeterminate
                                }
                            }
                        }
                        SelectionState.Enabled(selections.size, selectAll)
                    }

                    else -> SelectionState.Disabled
                }

                val entryRows = delivery.entries.map { e ->
                    val totalWeight = calculateEntryTotalWeight(e)
                    val totalCostCents = "$" + formatCents(calculateEntryTotalCostCents(e))
                    EntryRowState(
                        e.id,
                        e.id in selections,
                        e.item.name,
                        e.program?.name,
                        e.account?.name,
                        e.unitCount.toString(),
                        totalWeight.toPounds().toString(),
                        totalCostCents,
                    )
                }

                val totalCount = (delivery.entries.sumOf { it.unitCount }).toString()
                val totalWeight = ((calculateDeliveryTotalWeightPounds(delivery) * 10).roundToLong() / 10f).toString()
                val subtotal = "$" + formatCents(calculateDeliverySubTotalCostCents(delivery))

                EntryTableState(
                    entryRows,
                    selectionState,
                    entryAction,
                    totalCount,
                    totalWeight,
                    subtotal
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun showAddEntry() {
        entryAction.value = EntryAction.Add
    }

    fun showEditEntry(entryId: Long) {
        when (val current = deliveryViewCoordinator.state.value) {
            is DeliveryViewState.Viewing -> {
                // TODO(P1): Better error handling ...
                val entry = current.currentDelivery.entries.find { it.id == entryId }!!
                val body = EntryBody(
                    entry.item.id,
                    entry.unitCount,
                    entry.unitWeight,
                    entry.costStatus,
                    entry.unitCostCents,
                    entry.itemWeight,
                    entry.itemsPerUnit,
                    entry.program?.id,
                    entry.account?.id,
                )

                entryAction.value = EntryAction.Edit(entry.id, body)
            }

            else -> {
                // TODO(P2): Error handling ...
            }
        }
    }

    fun showDeleteEntry(entryId: Long) {
        entryAction.value = EntryAction.Delete(entryId)
    }

    fun cancelEntryOperation() {
        entryAction.value = null
    }

    // TODO: These can probably all be moved into a state-holder for the data table
    fun enableEntrySelection() {
        selectionEnabled.value = true
    }

    fun disableEntrySelection() {
        selectionEnabled.value = false
    }

    fun onToggleAllEntriesSelection() {
        val current = deliveryViewCoordinator.state.value

        if (current !is DeliveryViewState.Viewing) {
            // TODO(P2): Error handling ...
            return
        }

        when (selectedEntryRows.value.size) {
            0 -> selectedEntryRows.value = current.currentDelivery.entries.map { it.id }.toSet()
            else -> selectedEntryRows.value = emptySet()
        }
    }

    fun onToggleEntrySelection(isSelected: Boolean, entryId: Long) {
        when {
            isSelected -> selectedEntryRows.value = selectedEntryRows.value.plus(entryId)
            else -> selectedEntryRows.value = selectedEntryRows.value.minus(entryId)
        }
    }

    fun clearEntrySelection() {
        selectedEntryRows.value = emptySet()
    }

    fun onBulkUpdateProgram(newProgramId: Long?) {
        val selection = selectedEntryRows.value

        if (selection.isEmpty()) {
            return
        }

        // TODO: Error handling
        viewModelScope.launch {
            for (entryId in selection) {
                deliveryRepository.updateDeliveryEntryProgram(entryId, newProgramId)
            }
        }
    }

    fun onBulkUpdateAccount(newAccountId: Long?) {
        val selection = selectedEntryRows.value

        if (selection.isEmpty()) {
            return
        }

        // TODO: Error handling
        viewModelScope.launch {
            for (entryId in selection) {
                deliveryRepository.updateDeliveryEntryPurchasingAccount(entryId, newAccountId)
            }
        }
    }

    // TODO: These can be moved into a `EntryMutationProvider`...?
    fun saveEntry(body: EntryBody) {
        val current = deliveryViewCoordinator.state.value

        if (current !is DeliveryViewState.Viewing) {
            // TODO(P2): Error handling ...
            return
        }

        viewModelScope.launch {
            deliveryRepository.appendDeliveryEntry(
                current.currentDelivery.id,
                body.itemId,
                body.unitCount,
                body.unitWeight,
                body.costStatus,
                body.unitCostCents,
                body.itemWeight,
                body.itemsPerUnit,
                body.programId,
                body.accountId,
            )
        }
    }

    fun updateEntry(
        entryId: Long,
        body: EntryBody,
    ) {
        viewModelScope.launch {
            deliveryRepository.updateDeliveryEntry(
                entryId,
                body.itemId,
                body.unitCount,
                body.unitWeight,
                body.costStatus,
                body.unitCostCents,
                body.itemWeight,
                body.itemsPerUnit,
                body.programId,
                body.accountId,
            )
        }
    }

    fun deleteEntryById(id: Long) {
        viewModelScope.launch {
            deliveryRepository.deleteDeliveryEntryById(id)
        }
    }

}
