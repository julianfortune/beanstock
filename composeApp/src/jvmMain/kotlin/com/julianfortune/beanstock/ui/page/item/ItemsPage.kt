package com.julianfortune.beanstock.ui.page.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.common.component.ConfirmDeleteEntityForm
import com.julianfortune.beanstock.ui.common.component.EntityOptionsDropdownMenu
import com.julianfortune.beanstock.ui.common.foundation.Dialog
import com.julianfortune.beanstock.ui.common.foundation.SideSheet
import com.julianfortune.beanstock.ui.common.layout.Collection
import com.julianfortune.beanstock.ui.feature.item.data.ItemBody
import com.julianfortune.beanstock.ui.page.item.ui.ItemForm
import com.julianfortune.beanstock.ui.page.namedentity.data.EntityOperation
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsPage(viewModel: ItemsPageViewModel = koinViewModel()) {
    val coroutineScope = rememberCoroutineScope()

    // TODO: Turn this into rich state object
    //  AND Proactively check if `Item` is referenced by any Deliveries to disable deletion
    val items by viewModel.items.collectAsState(emptyList())
    val categoryOptions by viewModel.categoryOptions.collectAsState(emptyList())

    val itemOperation by viewModel.itemOperation

    Column {
        Collection(
            "Items",
            items,
            null,
            onClickCreateNew = {
                viewModel.showNewItem()
            }
        ) { item, modifier, elevation ->
            ListItem(
                headlineContent = {
                    Text(item.name)
                },
                modifier = modifier.clickable(
                    enabled = true,
                    onClick = {
                        viewModel.showEditItem(item)
                    }
                ),
                tonalElevation = elevation,
                trailingContent = {
                    EntityOptionsDropdownMenu(
                        edit = {
                            viewModel.showEditItem(item)
                        },
                        delete = {
                            viewModel.showDeleteItem(item)
                        },
                    )
                }
            )
        }
    }

    SideSheet(
        isVisible = itemOperation is EntityOperation.CreateNew,
        onDismissRequest = { viewModel.dismissItemModal() },
        modifier = Modifier.width(640.dp),
    ) {
        ItemForm(
            categoryOptions,
            "Create Item",
            "Create",
            onCancel = viewModel::cancelItemOperation,
        ) { body ->
            coroutineScope.launch {
                viewModel.saveItem(body)
                viewModel.dismissItemModal()
            }
        }
    }

    SideSheet(
        state = itemOperation as? EntityOperation.Edit,
        onDismissRequest = { viewModel.dismissItemModal() },
        modifier = Modifier.width(640.dp),
    ) { editOperation ->
        // TODO: Conversion in VM
        val itemBody = ItemBody(
            editOperation.entity.name,
            editOperation.entity.categories.firstOrNull()?.id,
            editOperation.entity.format,
        )

        ItemForm(
            categoryOptions,
            "Edit Item",
            "Save",
            itemBody,
            onCancel = viewModel::cancelItemOperation,
        ) { body ->
            coroutineScope.launch {
                viewModel.updateItem(editOperation.entity.id, body)
                viewModel.dismissItemModal()
            }
        }
    }

    // Delete modal
    if (itemOperation is EntityOperation.Delete) {
        Dialog(
            onDismissRequest = { viewModel.cancelItemOperation() }
        ) {
            val itemId = (itemOperation as EntityOperation.Delete).id
            ConfirmDeleteEntityForm(
                itemId,
                "Delete Item",
                onCancel = {
                    viewModel.cancelItemOperation()
                },
                onConfirm = {
                    coroutineScope.launch {
                        viewModel.deleteItem(itemId)
                        viewModel.dismissItemModal()
                    }
                }
            )
        }
    }
}
