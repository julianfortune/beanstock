package com.julianfortune.beanstock.ui.feature.delivery.form

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
import com.julianfortune.beanstock.ui.common.data.FormFieldState
import com.julianfortune.beanstock.ui.common.data.Option
import com.julianfortune.beanstock.ui.common.input.*
import com.julianfortune.beanstock.ui.feature.delivery.form.data.DeliveryBody
import com.julianfortune.beanstock.ui.feature.delivery.form.data.DeliveryFormState
import com.julianfortune.beanstock.ui.theme.AppPreview


@Composable
fun DeliveryForm(
    title: String,
    supplierOptions: List<Option<Long>>,
    initialDelivery: DeliveryBody?,
    onCancel: () -> Unit,
    onSubmit: (delivery: DeliveryBody) -> Unit,
    modifier: Modifier = Modifier
) {
    val stateHolder = remember(initialDelivery) {
        DeliveryFormStateHolder(
            initialDelivery
        )
    }

    DeliveryFormUi(
        title = title,
        supplierOptions = supplierOptions,
        state = stateHolder.uiState,
        onReceivedDateChange = stateHolder::onReceivedDateChange,
        onSupplierIdChange = stateHolder::onSupplierIdChange,
        onFeesChange = stateHolder::onFeesChange,
        onTaxesChange = stateHolder::onTaxesChange,
        onCancel = onCancel,
        onSubmit = {
            // `onSubmit` should only be invoked when `validData` is defined
            stateHolder.validData?.let { onSubmit(it) }
        },
        modifier = modifier
    )
}

@Composable
fun DeliveryFormUi(
    title: String,
    supplierOptions: List<Option<Long>>,
    state: DeliveryFormState,
    onReceivedDateChange: (LocalDateInput?) -> Unit = {},
    onReceivedDateFocusLost: () -> Unit = {},
    onSupplierIdChange: (Long?) -> Unit = {},
    onFeesChange: (CurrencyInput) -> Unit = {},
    onTaxesChange: (CurrencyInput) -> Unit = {},
    onCancel: () -> Unit = {},
    onSubmit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Date Received Field
        LocalDateInputTextField(
            value = state.receivedDate.value,
            onValueChange = onReceivedDateChange,
            onFocusLost = onReceivedDateFocusLost,
            label = { Text("Received*") },
            isError = state.receivedDate.isError,
            modifier = Modifier.fillMaxWidth(),
        )

        // Supplier Field
        AutocompleteSelect(
            selectedOptionId = state.selectedSupplierId.value,
            options = supplierOptions,
            onSelectedChange = { onSupplierIdChange(it?.id) },
            label = { Text("Supplier*") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fees Field
            CurrencyInputTextField(
                value = state.fees.value,
                onValueChange = onFeesChange,
                label = { Text("Fees") },
                modifier = Modifier.weight(1f),
            )

            Spacer(Modifier.width(8.dp))

            // Taxes Field
            CurrencyInputTextField(
                value = state.taxes.value,
                onValueChange = onTaxesChange,
                label = { Text("Taxes") },
                modifier = Modifier.weight(1f),
            )
        }

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
fun DeliveryFormPreview() {
    AppPreview {
        Column(modifier = Modifier.padding(16.dp)) {
            DeliveryFormUi(
                "New Delivery",
                listOf(Option(1, "Placeholder")),
                DeliveryFormState(),
                {},
                {},
            )
        }
    }
}


@Preview
@Composable
fun DeliveryFormValidPreview() {
    AppPreview {
        Column(modifier = Modifier.padding(16.dp)) {
            DeliveryFormUi(
                "Edit Delivery",
                listOf(Option(1, "Placeholder")),
                DeliveryFormState(
                    FormFieldState(LocalDateInput.ofToday().value),
                    FormFieldState(1),
                    isValid = true,
                ),
                {},
                {},
            )
        }
    }
}
