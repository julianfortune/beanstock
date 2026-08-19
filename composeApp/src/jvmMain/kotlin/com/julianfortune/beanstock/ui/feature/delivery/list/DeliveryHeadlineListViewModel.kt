package com.julianfortune.beanstock.ui.feature.delivery.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.beanstock.data.repository.DeliveryRepository
import com.julianfortune.beanstock.ui.coordinator.delivery.DeliveryViewCoordinator
import com.julianfortune.beanstock.ui.coordinator.delivery.data.DeliveryViewState
import com.julianfortune.beanstock.ui.delegate.SupplierOptionsProvider
import com.julianfortune.beanstock.ui.feature.delivery.form.data.DeliveryBody
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryHeadlineListViewModel(
    private val deliveryViewCoordinator: DeliveryViewCoordinator,
    private val deliveryRepository: DeliveryRepository,
    supplierOptionsProvider: SupplierOptionsProvider
) : ViewModel(), SupplierOptionsProvider by supplierOptionsProvider {

    sealed interface UiEvent {
        data class DeliveryCreated(val id: Long) : UiEvent
    }

    private val _uiEventChannel = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent: StateFlow<UiEvent?> = _uiEventChannel.receiveAsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    val selectedId = deliveryViewCoordinator.state.map {
        when (it) {
            DeliveryViewState.Empty -> null
            DeliveryViewState.Loading -> null
            is DeliveryViewState.Viewing -> it.currentDelivery.id
        }
    }

    // TODO(P3): Sorting, default: By receivedDate and then createdDatetime
    // TODO(P5): Filtering, e.g., by time period
    val allDeliveries = deliveryRepository.getAllAsHeadlines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun onSelect(id: Long) {
        deliveryViewCoordinator.view(id)
    }

    fun saveNewDelivery(delivery: DeliveryBody) {
        viewModelScope.launch {
            val result = deliveryRepository.insertDelivery(
                delivery.received,
                delivery.supplierId,
                delivery.taxesCents,
                delivery.feesCents,
            )

            result.map { newId ->
                _uiEventChannel.send(UiEvent.DeliveryCreated(newId))
                deliveryViewCoordinator.view(newId)
            }
            // TODO(P3): Error handling
        }
    }
}
