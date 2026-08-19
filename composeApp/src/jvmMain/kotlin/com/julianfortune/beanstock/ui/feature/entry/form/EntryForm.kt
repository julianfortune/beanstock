package com.julianfortune.beanstock.ui.feature.entry.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.common.data.Option
import com.julianfortune.beanstock.ui.common.input.AutocompleteSelect
import com.julianfortune.beanstock.ui.common.input.CurrencyInputTextField
import com.julianfortune.beanstock.ui.common.input.DropdownSelect
import com.julianfortune.beanstock.ui.feature.entry.form.data.*
import com.julianfortune.beanstock.ui.theme.AppPreview
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun EntryForm(
    initialEntry: EntryBody?,
    onCancel: () -> Unit,
    onSubmit: (body: EntryBody) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EntryFormViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val validData by viewModel.validData.collectAsState()

    val itemOptions by viewModel.itemOptions.collectAsState(emptyList())
    val programOptions by viewModel.programOptions.collectAsState(emptyList())
    val accountOptions by viewModel.accountOptions.collectAsState(emptyList())

    LaunchedEffect(initialEntry) {
        viewModel.setInitialEntry(initialEntry)
    }

    NewEntryFormUi(
        state = state,
        itemOptions = itemOptions,
        programOptions = programOptions,
        accountOptions = accountOptions,
        eventHandler = { event ->
            when (event) {
                is EntryFormEvent.AbortForm -> {
                    onCancel()
                }

                is EntryFormEvent.SubmitForm -> {
                    validData?.let { onSubmit(it) }
                }

                else -> {
                    viewModel.onEvent(event)
                }
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryFormUi(
    state: EntryFormState,
    itemOptions: List<Option<Long>> = emptyList(),
    programOptions: List<Option<Long>> = emptyList(),
    accountOptions: List<Option<Long>> = emptyList(),
    eventHandler: (EntryFormEvent) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = state.title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Column {
            AutocompleteSelect(
                selectedOptionId = state.selectedItemId,
                options = itemOptions,
                onSelectedChange = {
                    eventHandler(EntryFormEvent.ItemSelected(it?.id))
                },
                label = { Text("Item") },
                modifier = Modifier.fillMaxWidth()
            )

            Column {
                Spacer(modifier = Modifier.height(16.dp))

                val options = (state.itemWeightOptions as? ItemWeightOptionsState.Enabled)?.options ?: emptyList()

                DropdownSelect(
                    selectedId = state.selectedItemWeightIndex,
                    label = "Size",
                    enabled = state.itemWeightOptions is ItemWeightOptionsState.Enabled,
                    options = options.plus(Option(null, "Other")),
                    onSelectedChange = {
                        eventHandler(EntryFormEvent.ItemWeightSelected(it.id))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Unit (e.g., Case)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (state.unitWeight) {
                is UnitWeightState.PackagedItems -> {
                    OutlinedTextField(
                        value = state.unitWeight.itemCount,
                        onValueChange = {
                            eventHandler(EntryFormEvent.ItemCountChanged(it))
                        },
                        label = { Text("Count") },
                        modifier = Modifier
                            .height(64.dp)
                            .fillMaxWidth(),
                        singleLine = true,
                    )
                }

                is UnitWeightState.LooseItems -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.unitWeight.unitWeightPounds,
                            onValueChange = {
                                eventHandler(EntryFormEvent.WeightPoundsChanged(it))
                            },
                            label = { Text("Lbs") },
                            modifier = Modifier
                                .height(64.dp)
                                .weight(1f),
                            singleLine = true,
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = state.unitWeight.unitWeightOunces,
                            onValueChange = {
                                eventHandler(EntryFormEvent.WeightOuncesChanged(it))
                            },
                            label = { Text("Oz") },
                            modifier = Modifier
                                .height(64.dp)
                                .weight(1f),
                            singleLine = true,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val noCostOption = Option(id = true, label = "No Cost")
                val paidOption = Option(id = false, label = "Paid")

                DropdownSelect(
                    selectedId = state.costStatusIsNoCost,
                    label = "Status",
                    options = listOf(paidOption, noCostOption),
                    onSelectedChange = { selection ->
                        eventHandler(EntryFormEvent.CostStatusChanged(selection.id))
                    },
                    modifier = Modifier.weight(0.3f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                CurrencyInputTextField(
                    label = { Text("Price") },
                    value = state.unitCost,
                    onValueChange = {
                        eventHandler(EntryFormEvent.UnitCostChanged(it))
                    },
                    onFocusLost = {
                        eventHandler(EntryFormEvent.UnitCostLostFocus)
                    },
                    enabled = !state.costStatusIsNoCost,
                    modifier = Modifier.weight(0.7f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Quantity",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.unitCount,
                onValueChange = {
                    eventHandler(EntryFormEvent.UnitCountChanged(it))
                },
                label = { Text("Count") },
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Associations",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            AutocompleteSelect(
                selectedOptionId = state.selectedProgramId,
                options = programOptions,
                onSelectedChange = {
                    eventHandler(EntryFormEvent.ProgramSelected(it?.id))
                },
                label = { Text("Program") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            AutocompleteSelect(
                selectedOptionId = state.selectedAccountId,
                options = accountOptions,
                onSelectedChange = {
                    eventHandler(EntryFormEvent.AccountSelected(it?.id))
                },
                label = { Text("Account") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    onClick = {
                        eventHandler(EntryFormEvent.AbortForm)
                    }
                ) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    enabled = state.isValid,
                    onClick = {
                        eventHandler(EntryFormEvent.SubmitForm)
                    },
                ) {
                    Text(state.submissionText)
                }
            }
        }
    }
}

@Preview
@Composable
fun EntryFormPreview() {
    AppPreview {
        Column(modifier = Modifier.padding(16.dp)) {
            NewEntryFormUi(
                state = EntryFormState(),
            )
        }
    }
}
