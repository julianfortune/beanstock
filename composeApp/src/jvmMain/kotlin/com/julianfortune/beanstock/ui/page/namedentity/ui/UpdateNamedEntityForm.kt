package com.julianfortune.beanstock.ui.page.namedentity.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.data.common.NamedEntity
import com.julianfortune.beanstock.ui.page.namedentity.NamedEntityPageViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T: NamedEntity> UpdateNamedEntityForm(
    viewModel: NamedEntityPageViewModel<T>,
    title: String,
    submitButtonText: String,
    initialData: T? = null,
    onSubmit: (updatedName: String) -> Unit
) {
    var name by remember { mutableStateOf(initialData?.name ?: "") }

    val isValid = remember(name) {
        name != ""
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
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
            isError = false,
            colors = OutlinedTextFieldDefaults.colors(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = {
                    viewModel.dismissOperation()
                }
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                enabled = isValid,
                onClick = {
                    onSubmit(name)
                },
            ) {
                Text(submitButtonText)
            }
        }
    }

}
