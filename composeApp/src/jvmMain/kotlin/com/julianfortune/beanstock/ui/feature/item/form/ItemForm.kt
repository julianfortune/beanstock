package com.julianfortune.beanstock.ui.page.item.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.data.model.Weight
import com.julianfortune.beanstock.ui.common.data.Option
import com.julianfortune.beanstock.ui.common.foundation.ScrollableColumn
import com.julianfortune.beanstock.ui.common.input.AutocompleteSelect
import com.julianfortune.beanstock.ui.common.input.DropdownSelect
import com.julianfortune.beanstock.ui.feature.item.data.ItemBody
import com.julianfortune.beanstock.ui.feature.item.data.ItemFormState
import com.julianfortune.beanstock.ui.feature.item.data.ItemFormatState
import com.julianfortune.beanstock.ui.feature.savedweight.form.SavedWeightInlineForm
import com.julianfortune.beanstock.ui.theme.AppPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemForm(
    categoryOptions: List<Option<Long>>,
    title: String,
    submitButtonText: String,
    initialValue: ItemBody? = null,
    onCancel: () -> Unit,
    onSubmit: (body: ItemBody) -> Unit,
) {
    val stateHolder = remember {
        ItemFormStateHolder(initialValue)
    }

    ItemFormUi(
        title,
        submitButtonText,
        categoryOptions,
        stateHolder.uiState,
        onNameChange = stateHolder::onNameChange,
        onCategoryIdChange = stateHolder::onCategoryIdChange,
        onIsLoosePackagingChange = stateHolder::onPackagingIsLooseChange,
        onAddWeight = stateHolder::onAddDiscretePackageSize,
        onDeleteWeight = stateHolder::onRemoveDiscretePackageSize,
        onCancel = onCancel,
        onSubmit = {
            println(stateHolder.validData)
            stateHolder.validData?.let { onSubmit(it) }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemFormUi(
    title: String,
    submitButtonText: String,
    categoryOptions: List<Option<Long>>,
    state: ItemFormState,
    onNameChange: (newName: String) -> Unit = {},
    onCategoryIdChange: (newId: Long?) -> Unit = {},
    onIsLoosePackagingChange: (isLoose: Boolean) -> Unit = {},
    onAddWeight: (Weight) -> Unit = {},
    onDeleteWeight: (index: Int) -> Unit = {},
    onCancel: () -> Unit = {},
    onSubmit: () -> Unit = {},
) {

    ScrollableColumn {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = state.name.value,
                    onValueChange = onNameChange,
                    label = { Text("Name *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    singleLine = true,
                    isError = false,
                    colors = OutlinedTextFieldDefaults.colors(),
                )

                AutocompleteSelect(
                    state.categoryId.value,
                    categoryOptions,
                    {
                        onCategoryIdChange(it?.id)
                    },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                )

                DropdownSelect(
                    state.format is ItemFormatState.Loose,
                    label = "Packaging",
                    options = listOf(
                        Option(true, "Loose"),
                        Option(false, "Packaged")
                    ),
                    onSelectedChange = {
                        onIsLoosePackagingChange(it.id)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                state.format is ItemFormatState.Packaged,
            ) {
                Column {
                    Text(
                        "Package sizes (by weight)",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )

                    OutlinedCard {
                        Column {
                            (state.format as? ItemFormatState.Packaged)?.sizes?.let { packagingVariants ->
                                packagingVariants.forEachIndexed { index, text ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .height(64.dp)
                                            .padding(start = 16.dp, end = 8.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(text, fontFamily = FontFamily.Monospace)

                                        IconButton(
                                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                                            onClick = { onDeleteWeight(index) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Clear,
                                                contentDescription = "Remove size"
                                            )
                                        }
                                    }

                                    HorizontalDivider(Modifier.height(1.dp))
                                }
                            }

                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                SavedWeightInlineForm(
                                    onSubmit = { body ->
                                        onAddWeight(body.weight)
                                    },
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    onClick = onCancel
                ) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    enabled = state.isValid,
                    onClick = onSubmit,
                ) {
                    Text(submitButtonText)
                }

            }
        }
    }

}

@Preview
@Composable
fun ItemFormUiPreview() = AppPreview {
    ItemFormUi(
        title = "Title",
        submitButtonText = "Save",
        categoryOptions = emptyList(),
        state = ItemFormState(
            format = ItemFormatState.Packaged(listOf("8oz", "1lb 8oz"))
        )
    )
}
