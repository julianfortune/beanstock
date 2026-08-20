package com.julianfortune.beanstock.ui.page.namedentity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.julianfortune.beanstock.data.common.NamedEntity
import com.julianfortune.beanstock.ui.common.component.ConfirmDeleteEntityForm
import com.julianfortune.beanstock.ui.common.component.EntityOptionsDropdownMenu
import com.julianfortune.beanstock.ui.common.foundation.Dialog
import com.julianfortune.beanstock.ui.common.layout.Collection
import com.julianfortune.beanstock.ui.page.namedentity.data.EntityOperation
import com.julianfortune.beanstock.ui.page.namedentity.ui.UpdateNamedEntityForm
import kotlinx.coroutines.launch


// TODO: Proactively check if entity is referenced by > 1 other entities and can't be deleted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : NamedEntity> NamedEntityPage(
    viewModel: NamedEntityPageViewModel<T>,
    title: String,
    entityName: String
) {
    val coroutineScope = rememberCoroutineScope()

    val entities by viewModel.allEntities.collectAsState(emptyList<T>())
    val currentOperation by viewModel.operation

    Column {
        Collection(
            title,
            entities,
            null,
            onClickCreateNew = {
                viewModel.showCreateNew()
            }
        ) { entity, modifier, elevation ->
            ListItem(
                headlineContent = {
                    Text(entity.name)
                },
                modifier = modifier.clickable(
                    enabled = true,
                    onClick = {
                        viewModel.showEdit(entity)
                    }
                ),
                tonalElevation = elevation,
                trailingContent = {
                    EntityOptionsDropdownMenu(
                        edit = {
                            viewModel.showEdit(entity)
                        },
                        delete = {
                            viewModel.showDelete(entity)
                        },
                    )
                }
            )
        }
    }

    // Modal
    if (currentOperation != null) {
        Dialog(
            onDismissRequest = {
                viewModel.dismissOperation()
            }
        ) {
            when (currentOperation) {
                is EntityOperation.CreateNew -> {
                    UpdateNamedEntityForm(viewModel, "Create $entityName", "Create") { newName ->
                        coroutineScope.launch {
                            viewModel.save(newName)
                            viewModel.dismissOperation()
                        }
                    }
                }

                is EntityOperation.Edit -> {
                    val original = (currentOperation as EntityOperation.Edit).entity

                    UpdateNamedEntityForm(
                        viewModel,
                        "Rename $entityName",
                        "Save",
                        original,
                    ) { newName ->
                        coroutineScope.launch {
                            viewModel.update(original.id, newName)
                            viewModel.dismissOperation()
                        }
                    }
                }

                is EntityOperation.Delete -> {
                    val id = (currentOperation as EntityOperation.Delete).id

                    ConfirmDeleteEntityForm(
                        id,
                        "Delete $entityName",
                        onCancel = {
                            viewModel.dismissOperation()
                        },
                        onConfirm = {
                            coroutineScope.launch {
                                viewModel.delete(id)
                                viewModel.dismissOperation()
                            }
                        }
                    )
                }

                else -> throw Error("`operation` must not be `null`")
            }
        }
    }
}
