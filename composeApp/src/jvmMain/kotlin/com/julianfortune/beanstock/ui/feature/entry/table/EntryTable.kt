package com.julianfortune.beanstock.ui.feature.entry.table

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Cases
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.julianfortune.beanstock.ui.common.component.ConfirmDeleteEntityForm
import com.julianfortune.beanstock.ui.common.component.EntityOptionsDropdownMenu
import com.julianfortune.beanstock.ui.common.data.Option
import com.julianfortune.beanstock.ui.common.foundation.Dialog
import com.julianfortune.beanstock.ui.common.foundation.ScrollableColumn
import com.julianfortune.beanstock.ui.common.input.AutocompleteSelect
import com.julianfortune.beanstock.ui.feature.entry.form.EntryForm
import com.julianfortune.beanstock.ui.feature.entry.table.data.EntryAction
import com.julianfortune.beanstock.ui.feature.entry.table.data.EntryTableState
import com.julianfortune.beanstock.ui.feature.entry.table.data.SelectionState
import com.julianfortune.beanstock.ui.feature.entry.table.ui.EntryRow
import com.julianfortune.beanstock.ui.feature.entry.table.ui.EntrySideSheet
import com.julianfortune.beanstock.ui.theme.AppPreview
import org.koin.compose.viewmodel.koinViewModel

private val horizontalContentPadding = 24.dp
private val horizontalTextPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryTable(
    viewModel: EntryTableViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val programOptions by viewModel.programOptions.collectAsState(emptyList())
    val accountOptions by viewModel.accountOptions.collectAsState(emptyList())

    Box {
        uiState?.let { state ->
            EntryTableUi(
                state,
                programOptions,
                accountOptions,
                viewModel::showAddEntry,
                viewModel::showEditEntry,
                viewModel::showDeleteEntry,
                viewModel::enableEntrySelection,
                viewModel::disableEntrySelection,
                viewModel::onToggleAllEntriesSelection,
                viewModel::onToggleEntrySelection,
                viewModel::onBulkUpdateProgram,
                viewModel::onBulkUpdateAccount,
                viewModel::clearEntrySelection,
            )

            EntrySideSheet(
                state = (state.action as? EntryAction.Add),
                onDismissRequest = { viewModel.cancelEntryOperation() },
            ) {
                ScrollableColumn {
                    EntryForm(
                        null,
                        onCancel = {
                            viewModel.cancelEntryOperation()
                        },
                        onSubmit = { entry ->
                            viewModel.saveEntry(entry)
                            viewModel.cancelEntryOperation()
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            EntrySideSheet(
                state = (state.action as? EntryAction.Edit),
                onDismissRequest = { viewModel.cancelEntryOperation() },
            ) { editAction ->
                ScrollableColumn {
                    EntryForm(
                        editAction.entry,
                        onCancel = {
                            viewModel.cancelEntryOperation()
                        },
                        onSubmit = { newEntry ->
                            viewModel.updateEntry(editAction.id, newEntry)
                            viewModel.cancelEntryOperation()
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            when (val action = state.action) {
                is EntryAction.Delete -> {
                    Dialog(
                        onDismissRequest = { viewModel.cancelEntryOperation() },
                    ) {
                        ConfirmDeleteEntityForm(
                            action.id,
                            "Delete Entry",
                            onCancel = {
                                viewModel.cancelEntryOperation()
                            },
                            onConfirm = { id ->
                                viewModel.deleteEntryById(id)
                                viewModel.cancelEntryOperation()
                            }
                        )
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
fun EntryTableUi(
    state: EntryTableState,
    programOptions: List<Option<Long>>,
    accountOptions: List<Option<Long>>,
    onClickAddEntry: () -> Unit = {},
    onClickEditEntry: (entryId: Long) -> Unit = {},
    onClickDeleteEntry: (entryId: Long) -> Unit = {},
    onClickEnableSelection: () -> Unit = {},
    onClickDisableSelection: () -> Unit = {},
    onClickToggleAllEntries: () -> Unit = {},
    onClickToggleEntry: (isSelected: Boolean, entryId: Long) -> Unit = { _, _ -> },
    onClickBulkUpdateProgram: (programId: Long?) -> Unit = {},
    onClickBulkUpdateAccount: (accountId: Long?) -> Unit = {},
    onClickClearEntrySelection: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(horizontal = horizontalContentPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Entries",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = horizontalTextPadding)
            )

            val actionRowHeight = 48.dp

            when (val selectionState = state.selection) {
                is SelectionState.Disabled -> {
                    Row {
                        IconButton(
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                            onClick = {
                                onClickEnableSelection()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = "Select multiple entries",
                            )
                        }
                    }
                }

                is SelectionState.Enabled -> {
                    Row(
                        modifier = Modifier.height(actionRowHeight),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(Modifier.width(16.dp))
                        Text(
                            "${selectionState.count} selected",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.width(16.dp))

                        BulkEditProgramDropdownButton(enabled = selectionState.count > 0) { dismiss ->
                            BulkEditLinkedProperty(
                                "Bulk edit program",
                                "Program*",
                                programOptions,
                            ) { newProgramId ->
                                onClickBulkUpdateProgram(newProgramId)
                                dismiss()
                            }
                        }

                        BulkEditAccountDropdownButton(enabled = selectionState.count > 0) { dismiss ->
                            BulkEditLinkedProperty(
                                "Bulk edit account",
                                "Account*",
                                accountOptions,
                            ) { newAccountId ->
                                onClickBulkUpdateAccount(newAccountId)
                                dismiss()
                            }
                        }

//                        IconButton(
//                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
//                            onClick = {
//
//                            }
//                        ) {
//                            Icon(
//                                imageVector = Icons.Outlined.Delete,
//                                contentDescription = "Delete entries",
//                            )
//                        }

                        IconButton(
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                            onClick = {
                                onClickClearEntrySelection()
                                onClickDisableSelection()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Cancel selection",
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = onClickAddEntry,
            ) {
                Text("New")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Header row
        EntryRow(
            modifier = Modifier.fillMaxWidth().height(32.dp),
        ) {
            val selectAllState = (state.selection as? SelectionState.Enabled)?.selectAll ?: ToggleableState.Off

            SelectionCell(state.selection is SelectionState.Enabled) {
                TriStateCheckbox(
                    modifier = Modifier
                        .size(28.dp)
                        .pointerHoverIcon(PointerIcon.Hand),
                    state = selectAllState,
                    onClick = {
                        onClickToggleAllEntries()
                    },
                )
            }
            ItemNameCell { EntryRowHeaderText("Item") }
            ProgramCell { EntryRowHeaderText("Program") }
            PurchasingAccountCell { EntryRowHeaderText("Account") }
            UnitCountCell { EntryRowHeaderText("Count") }
            EntryWeightCell { EntryRowHeaderText("Weight") }
            EntryCostCell { EntryRowHeaderText("Cost") }
            ActionCell { }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.dp
        )

        state.rows.forEach { entryRow ->
            EntryRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 12.dp,
                    ),
            ) {
                SelectionCell(state.selection is SelectionState.Enabled) {
                    Checkbox(
                        modifier = Modifier
                            .size(28.dp)
                            .pointerHoverIcon(PointerIcon.Hand),
                        checked = entryRow.isSelected,
                        onCheckedChange = {
                            onClickToggleEntry(it, entryRow.entryId)
                        },
                    )
                }
                ItemNameCell { Text(entryRow.itemName) }
                ProgramCell { Text(entryRow.programName ?: "") }
                PurchasingAccountCell { Text(entryRow.accountName ?: "") }
                UnitCountCell {
                    Text(
                        text = entryRow.unitCount,
                        fontFamily = FontFamily.Monospace,
                    )
                }
                EntryWeightCell {
                    Text(
                        text = entryRow.totalWeight,
                        fontFamily = FontFamily.Monospace,
                    )
                }
                EntryCostCell {
                    Text(
                        text = entryRow.totalCost,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                    )
                }
                ActionCell {
                    EntityOptionsDropdownMenu(
                        edit = { onClickEditEntry(entryRow.entryId) },
                        delete = { onClickDeleteEntry(entryRow.entryId) }
                    )
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp
            )
        }

        Column(
            modifier = Modifier
                .pointerHoverIcon(PointerIcon.Hand)
                .clickable { onClickAddEntry() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "New",
                Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp
            )
        }

        // Footer
        EntryRow(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 12.dp,
                ),
        ) {
            SelectionCell(state.selection is SelectionState.Enabled) {}
            ItemNameCell { }
            ProgramCell { }
            PurchasingAccountCell { }
            UnitCountCell {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "UNITS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    )
                    SelectionContainer {
                        Text(
                            text = state.totalCount,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            }
            EntryWeightCell {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "LBS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    )
                    SelectionContainer {
                        Text(
                            text = state.totalWeight,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            }
            EntryCostCell {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "SUBTOTAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    )
                    SelectionContainer {
                        Text(
                            text = state.totalCost,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            }
            ActionCell { }
        }
    }
}

@Composable
fun EntryRowHeaderText(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
    )
}

@Composable
fun BulkEditProgramDropdownButton(
    enabled: Boolean = true,
    content: @Composable (dismiss: () -> Unit) -> Unit,
) {
    SelectionModifierMenu(
        icon = {
            Icon(
                imageVector = Icons.Outlined.Cases,
                contentDescription = "Edit Program",
            )
        },
        tooltipText = "Edit Program",
        enabled = enabled,
    ) { dismiss ->
        content(dismiss)
    }
}


@Composable
fun BulkEditAccountDropdownButton(
    enabled: Boolean = true,
    content: @Composable (dismiss: () -> Unit) -> Unit,
) {
    SelectionModifierMenu(
        icon = {
            Icon(
                imageVector = Icons.Outlined.AccountBalanceWallet,
                contentDescription = "Edit Purchasing Account",
            )
        },
        tooltipText = "Edit Purchasing Account",
        enabled = enabled,
    ) { dismiss ->
        content(dismiss)
    }
}

@Composable
fun BulkEditLinkedProperty(
    title: String,
    label: String,
    options: List<Option<Long>>,
    onSubmit: (newProgramId: Long?) -> Unit
) {
    var selectedId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .width(320.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title)

        AutocompleteSelect(
            selectedId,
            options,
            onSelectedChange = {
                selectedId = it?.id
            },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Button(
                onClick = {
                    onSubmit(selectedId)
                },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Text("Save")
            }
        }
    }
}

@Preview
@Composable
fun BulkEditLinkedPropertyPreview() = AppPreview {
    BulkEditLinkedProperty(
        "Edit Program",
        "Program",
        listOf(Option(1L, "Placeholder")),
        {}
    )
}


// TODO: This should live in common.foundation / common.component (and maybe be multiple)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionModifierMenu(
    icon: @Composable () -> Unit,
    tooltipText: String,
    enabled: Boolean = true,
    content: @Composable (dismiss: () -> Unit) -> Unit
) {
    var programPopoverOpen by remember { mutableStateOf(false) }

    Column {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip { Text(tooltipText) }
            },
            state = rememberTooltipState()
        ) {
            IconButton(
                onClick = { programPopoverOpen = !programPopoverOpen },
                enabled = enabled,
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                icon()
            }
        }

        // TODO(P3): Try to fix alignment
        Box {
            if (programPopoverOpen) {
                Popup(
                    alignment = Alignment.TopCenter,
                    // Dismisses the popup when clicking outside
                    onDismissRequest = { programPopoverOpen = false },
                    properties = PopupProperties(focusable = true)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        OutlinedCard(
                            colors = CardDefaults.cardColors().copy(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        ) {
                            content { programPopoverOpen = false }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun EntryTablePreviewEmpty() = AppPreview {
    EntryTableUi(
        EntryTableState.empty(),
        emptyList(),
        emptyList(),
    )
}
