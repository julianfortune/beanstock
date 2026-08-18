package com.julianfortune.glacier.ui.feature.report.detail.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.common.data.Option
import com.julianfortune.glacier.ui.common.input.AutocompleteSelect
import com.julianfortune.glacier.ui.common.input.DropdownSelect
import com.julianfortune.glacier.ui.common.input.LocalDateInput
import com.julianfortune.glacier.ui.common.input.LocalDateInputTextField
import com.julianfortune.glacier.ui.feature.report.detail.data.EditReportCriteriaFormState
import com.julianfortune.glacier.ui.feature.report.detail.data.ReportCriteriaBody
import com.julianfortune.glacier.ui.theme.AppPreview

@Composable
fun EditReportCriteriaForm(
    initialValues: ReportCriteriaBody,
    itemOptions: List<Option<Long>> = emptyList(),
    categoryOptions: List<Option<Long>> = emptyList(),
    programOptions: List<Option<Long>> = emptyList(),
    purchasingAccountOptions: List<Option<Long>> = emptyList(),
    supplierOptions: List<Option<Long>> = emptyList(),
    onCancel: () -> Unit = {},
    onSubmit: (newReport: ReportCriteriaBody) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stateHolder = remember {
        EditReportCriteriaStateHolder(initialValues)
    }

    EditReportCriteriaFormUi(
        stateHolder.uiState,
        itemOptions,
        categoryOptions,
        programOptions,
        purchasingAccountOptions,
        supplierOptions,
        onStartDateChange = stateHolder::onStartDateChanged,
        onStartDateFocusLost = stateHolder::onStartDateFocusLost,
        onEndDateChange = stateHolder::onEndDateChanged,
        onEndDateFocusLost = stateHolder::onEndDateFocusLost,
        onItemIdChange = stateHolder::onItemIdChanged,
        onCategoryIdChange = stateHolder::onCategoryIdChanged,
        onCostStatusChange = stateHolder::onCostStatusIsNoCostChanged,
        onProgramIdChange = stateHolder::onProgramIdChanged,
        onPurchasingAccountIdChange = stateHolder::onPurchasingAccountIdChanged,
        onSupplierIdChange = stateHolder::onSupplierIdChanged,
        onCancel = onCancel,
        onSubmit = {
            // `onSubmit` should only be invoked when `validData` is defined
            stateHolder.validData?.let { onSubmit(it) }
        },
        modifier = modifier,
    )
}

@Composable
fun EditReportCriteriaFormUi(
    state: EditReportCriteriaFormState,
    itemOptions: List<Option<Long>> = emptyList(),
    categoryOptions: List<Option<Long>> = emptyList(),
    programOptions: List<Option<Long>> = emptyList(),
    purchasingAccountOptions: List<Option<Long>> = emptyList(),
    supplierOptions: List<Option<Long>> = emptyList(),
    onStartDateChange: (LocalDateInput?) -> Unit = {},
    onStartDateFocusLost: () -> Unit = {},
    onEndDateChange: (LocalDateInput?) -> Unit = {},
    onEndDateFocusLost: () -> Unit = {},
    onItemIdChange: (itemId: Long?) -> Unit = {},
    onCategoryIdChange: (categoryId: Long?) -> Unit = {},
    onCostStatusChange: (isNoCost: Boolean?) -> Unit = {},
    onProgramIdChange: (programId: Long?) -> Unit = {},
    onPurchasingAccountIdChange: (accountId: Long?) -> Unit = {},
    onSupplierIdChange: (supplierId: Long?) -> Unit = {},
    onCancel: () -> Unit = {},
    onSubmit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Edit Criteria",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LocalDateInputTextField(
                value = state.startDate.value,
                onValueChange = onStartDateChange,
                onFocusLost = onStartDateFocusLost,
                label = { Text("Start*") },
                isError = state.startDate.isError,
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(16.dp))

            LocalDateInputTextField(
                value = state.endDate.value,
                onValueChange = onEndDateChange,
                onFocusLost = onEndDateFocusLost,
                label = { Text("End*") },
                isError = state.endDate.isError,
                modifier = Modifier.weight(1f),
            )
        }

        AutocompleteSelect(
            selectedOptionId = state.itemId.value,
            options = itemOptions,
            onSelectedChange = { onItemIdChange(it?.id) },
            label = { Text("Item") },
            modifier = Modifier.fillMaxWidth()
        )

        AutocompleteSelect(
            selectedOptionId = state.categoryId.value,
            options = categoryOptions,
            onSelectedChange = { onCategoryIdChange(it?.id) },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(0.dp))

        val costStatusOptions = listOf(
            Option<Boolean?>(id = null, label = "Any"),
            Option<Boolean?>(id = false, label = "Paid"),
            Option<Boolean?>(id = true, label = "No Cost"),
        )

        DropdownSelect(
            label = "Cost Status",
            options = costStatusOptions,
            selectedId = state.costStatusIsNoCost.value,
            onSelectedChange = { isNoCost ->
                onCostStatusChange(isNoCost.id)
            },
        )

        AutocompleteSelect(
            selectedOptionId = state.programId.value,
            options = programOptions,
            onSelectedChange = { onProgramIdChange(it?.id) },
            label = { Text("Program") },
            modifier = Modifier.fillMaxWidth()
        )

        AutocompleteSelect(
            selectedOptionId = state.accountId.value,
            options = purchasingAccountOptions,
            onSelectedChange = { onPurchasingAccountIdChange(it?.id) },
            label = { Text("Account") },
            modifier = Modifier.fillMaxWidth()
        )

        AutocompleteSelect(
            selectedOptionId = state.supplierId.value,
            options = supplierOptions,
            onSelectedChange = { onSupplierIdChange(it?.id) },
            label = { Text("Supplier") },
            modifier = Modifier.fillMaxWidth()
        )

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = onCancel,
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                enabled = state.isValid,
                onClick = onSubmit,
            ) {
                Text("Save")
            }
        }
    }
}

@Preview
@Composable
fun EditReportCriteriaFormUiPreview() = AppPreview {
    EditReportCriteriaFormUi(EditReportCriteriaFormState())
}
