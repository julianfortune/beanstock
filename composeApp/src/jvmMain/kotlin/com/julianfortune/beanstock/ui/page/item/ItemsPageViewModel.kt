package com.julianfortune.beanstock.ui.page.item

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.beanstock.data.model.Item
import com.julianfortune.beanstock.data.model.ItemHeadline
import com.julianfortune.beanstock.data.repository.ItemRepository
import com.julianfortune.beanstock.ui.delegate.CategoryOptionsProvider
import com.julianfortune.beanstock.ui.feature.item.data.ItemBody
import com.julianfortune.beanstock.ui.page.namedentity.data.EntityOperation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ItemsPageViewModel(
    private val itemRepository: ItemRepository,
    categoryOptionsProvider: CategoryOptionsProvider,
) : ViewModel(),
    CategoryOptionsProvider by categoryOptionsProvider {

    private val _itemOperation = mutableStateOf<EntityOperation<Item>?>(null)
    val itemOperation: State<EntityOperation<Item>?> = _itemOperation

    val items = itemRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    suspend fun saveItem(body: ItemBody) {
        itemRepository.insert(
            body.name,
            setOfNotNull(body.categoryId),
            body.format,
        )
    }

    suspend fun updateItem(id: Long, body: ItemBody) {
        itemRepository.update(
            id,
            body.name,
            setOfNotNull(body.categoryId),
            body.format,
        )
    }

    suspend fun deleteItem(id: Long) {
        // TODO: Error handling
        itemRepository.deleteById(id).onFailure {
            println(">> Error=$it")
        }
    }

    fun showNewItem() {
        _itemOperation.value = EntityOperation.CreateNew
    }

    fun showEditItem(item: ItemHeadline) {
        viewModelScope.launch {
            val fullItem = itemRepository.getById(item.id).firstOrNull()
            fullItem?.let { _itemOperation.value = EntityOperation.Edit(it) }
        }
    }

    fun showDeleteItem(item: ItemHeadline) {
        _itemOperation.value = EntityOperation.Delete(item.id)
    }

    fun dismissItemModal() {
        _itemOperation.value = null
    }

    fun cancelItemOperation() {
        _itemOperation.value = null
    }
}
