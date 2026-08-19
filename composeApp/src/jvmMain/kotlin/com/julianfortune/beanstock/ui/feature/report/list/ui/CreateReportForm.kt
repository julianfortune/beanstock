package com.julianfortune.beanstock.ui.feature.report.list.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.common.input.LocalDateInput
import com.julianfortune.beanstock.ui.common.input.LocalDateInputTextField
import com.julianfortune.beanstock.ui.feature.report.list.data.CreateReportBody
import com.julianfortune.beanstock.ui.feature.report.list.data.CreateReportFormState
import com.julianfortune.beanstock.ui.theme.AppPreview

@Composable
fun CreateReportForm(
    onCancel: () -> Unit,
    onSubmit: (newReport: CreateReportBody) -> Unit,
    modifier: Modifier = Modifier
) {
    val stateHolder = remember {
        CreateReportFormStateHolder()
    }

    CreateReportFormUi(
        stateHolder.uiState,
        onNameChange = stateHolder::onNameChanged,
        onStartDateChange = stateHolder::onStartDateChanged,
        onStartDateFocusLost = stateHolder::onStartDateFocusLost,
        onEndDateChange = stateHolder::onEndDateChanged,
        onEndDateFocusLost = stateHolder::onEndDateFocusLost,
        onCancel = onCancel,
        onSubmit = {
            // `onSubmit` should only be invoked when `validData` is defined
            stateHolder.validData?.let { onSubmit(it) }
        },
        modifier = modifier,
    )
}

@Composable
fun CreateReportFormUi(
    state: CreateReportFormState,
    onNameChange: (String) -> Unit = {},
    onStartDateChange: (LocalDateInput?) -> Unit = {},
    onStartDateFocusLost: () -> Unit = {},
    onEndDateChange: (LocalDateInput?) -> Unit = {},
    onEndDateFocusLost: () -> Unit = {},
    onCancel: () -> Unit = {},
    onSubmit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Create Report",
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
                .height(64.dp)
                .onFocusChanged({ state ->
                    if (!state.isFocused) {
                        // Check for error
                    }
                }),
            singleLine = true,
            isError = state.name.isError,
            colors = OutlinedTextFieldDefaults.colors(),
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
fun CreateReportFormUiPreview() = AppPreview {
    CreateReportFormUi(CreateReportFormState())
}
