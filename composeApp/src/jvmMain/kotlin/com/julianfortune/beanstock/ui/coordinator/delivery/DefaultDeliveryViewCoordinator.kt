package com.julianfortune.beanstock.ui.coordinator.delivery

import com.julianfortune.beanstock.data.model.Delivery
import com.julianfortune.beanstock.data.repository.DeliveryRepository
import com.julianfortune.beanstock.ui.coordinator.delivery.data.DeliveryViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultDeliveryViewCoordinator(
    deliveryRepository: DeliveryRepository,
    coroutineScope: CoroutineScope,
) : DeliveryViewCoordinator {

    private val _targetDeliveryId = MutableStateFlow<Long?>(null)

    private val _delivery: Flow<Delivery?> = _targetDeliveryId.flatMapLatest { id ->
        id?.let { deliveryRepository.getDeliveryById(it) } ?: flowOf(null)
    }

    override val state: StateFlow<DeliveryViewState> = combine(_delivery, _targetDeliveryId) { delivery, targetId ->
        when {
            targetId == null -> DeliveryViewState.Empty
            delivery != null && delivery.id == targetId -> DeliveryViewState.Viewing(delivery)
            else -> DeliveryViewState.Loading
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DeliveryViewState.Empty,
    )

    override fun view(deliveryId: Long) {
        _targetDeliveryId.value = deliveryId
    }

    override fun clear() {
        _targetDeliveryId.value = null
    }
}
