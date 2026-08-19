package com.julianfortune.beanstock.ui.common.input

import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

sealed interface LocalDateInput {

    val value: String

    companion object {
        fun of(localDate: LocalDate): Valid {
            return Valid(localDate.format(FORMATTER), localDate)
        }

        fun ofToday(): Valid {
            val today = LocalDate.now()
            return Valid(today.format(FORMATTER), today)
        }

        fun fromInput(input: String): LocalDateInput = when (val result = parseDateSafe(input)) {
            null -> Invalid(input)
            else -> Valid(input, result)
        }
    }

    data class Valid(override val value: String, val parsed: LocalDate) : LocalDateInput

    @JvmInline
    value class Invalid(override val value: String) : LocalDateInput
}

val FORMATTER: DateTimeFormatter = DateTimeFormatter
    .ofPattern("MM/dd/uuuu")
    .withResolverStyle(ResolverStyle.STRICT) // Rejects invalid dates like Feb 30

fun parseDateSafe(input: String): LocalDate? {
    if (input.isBlank()) return null

    return runCatching { LocalDate.parse(input, FORMATTER) }.getOrNull()
}

@Composable
fun LocalDateInputTextField(
    value: String?,
    onValueChange: (LocalDateInput?) -> Unit,
    label: @Composable (() -> Unit)? = { Text("Date") },
    enabled: Boolean = true,
    isError: Boolean = false,
    onFocusLost: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value ?: "",
        onValueChange = { newValue ->
            if (newValue == "") onValueChange(null)
            onValueChange(LocalDateInput.fromInput(newValue))
        },
        label = label,
        modifier = modifier
            .height(86.dp)
            .onFocusChanged({ state ->
                if (!state.isFocused) {
                    // Check for error / format / etc.
                    onFocusLost()
                }
            }),
        singleLine = true,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(),
        supportingText = { Text("MM/DD/YYYY") },
        enabled = enabled,
    )
}