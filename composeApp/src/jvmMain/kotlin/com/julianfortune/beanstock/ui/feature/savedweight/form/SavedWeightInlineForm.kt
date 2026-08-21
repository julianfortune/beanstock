package com.julianfortune.beanstock.ui.feature.savedweight.form

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.common.data.FormFieldState
import com.julianfortune.beanstock.ui.common.input.CompactOutlinedTextField
import com.julianfortune.beanstock.ui.feature.savedweight.form.data.SavedWeightBody
import com.julianfortune.beanstock.ui.feature.savedweight.form.data.SavedWeightFormState
import com.julianfortune.beanstock.ui.theme.AppPreview


@Composable
fun SavedWeightInlineForm(
    onSubmit: (SavedWeightBody) -> Unit = {}
) {
    val stateHolder = remember {
        SavedWeightStateHolder()
    }

    SavedWeightInlineFormUi(
        stateHolder.uiState,
        stateHolder::onPoundsChange,
        stateHolder::onOuncesChange,
        onSubmit = {
            stateHolder.validData?.let { onSubmit(it) }
            stateHolder.clear()
        },
    )
}

@Composable
fun SavedWeightInlineFormUi(
    state: SavedWeightFormState,
    onPoundsValueChange: (String) -> Unit = {},
    onOuncesValueChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        CompactOutlinedTextField(
            value = state.lbs.value,
            onValueChange = onPoundsValueChange,
            units = "lb",
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        CompactOutlinedTextField(
            value = state.oz.value,
            onValueChange = onOuncesValueChange,
            units = "oz",
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            enabled = state.isValid,
            onClick = onSubmit,
        ) {
            Text("Add")
        }
    }
}

@Preview
@Composable
fun SavedWeightInlineFormPreview() = AppPreview {
    SavedWeightInlineFormUi(
        SavedWeightFormState(
            FormFieldState("80"),
            FormFieldState("zzz", isError = true)
        )
    )
}
