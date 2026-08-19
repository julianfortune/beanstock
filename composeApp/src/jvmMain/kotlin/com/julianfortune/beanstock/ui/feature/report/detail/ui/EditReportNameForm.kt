package com.julianfortune.beanstock.ui.feature.report.detail.ui


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
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.feature.report.detail.data.EditReportNameFormState
import com.julianfortune.beanstock.ui.feature.report.detail.data.ReportNameBody

@Composable
fun EditReportCriteriaForm(
    initialValues: ReportNameBody,
    onCancel: () -> Unit = {},
    onSubmit: (newName: ReportNameBody) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stateHolder = remember {
        EditReportNameStateHolder(initialValues)
    }

    EditReportCriteriaFormUi(
        stateHolder.uiState,
        onNameChange = stateHolder::onNameChanged,
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
    state: EditReportNameFormState,
    onNameChange: (name: String) -> Unit = {},
    onCancel: () -> Unit = {},
    onSubmit: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Edit Name",
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
