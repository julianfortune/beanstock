package com.julianfortune.beanstock.ui.feature.report.list.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.julianfortune.beanstock.ui.common.data.FormFieldState
import com.julianfortune.beanstock.ui.common.input.LocalDateInput
import com.julianfortune.beanstock.ui.feature.report.list.data.CreateReportBody
import com.julianfortune.beanstock.ui.feature.report.list.data.CreateReportFormState

class CreateReportFormStateHolder {

    private var nameInput by mutableStateOf("")
    private var startDateInput by mutableStateOf<LocalDateInput?>(null)
    private var endDateInput by mutableStateOf<LocalDateInput?>(null)

    val validData: CreateReportBody? by derivedStateOf {
        val name = nameInput
        val startDate = startDateInput
        val endDate = endDateInput

        if (name != "" && startDate is LocalDateInput.Valid && endDate is LocalDateInput.Valid) {
            CreateReportBody(
                name,
                startDate.parsed,
                endDate.parsed,
            )
        } else {
            null
        }
    }

    val uiState: CreateReportFormState by derivedStateOf {
        CreateReportFormState(
            name = FormFieldState(nameInput),
            startDate = FormFieldState(startDateInput?.value ?: ""),
            endDate = FormFieldState(endDateInput?.value ?: ""),
            isValid = validData != null,
        )
    }

    fun onNameChanged(name: String) {
        nameInput = name
    }

    fun onStartDateChanged(startDate: LocalDateInput?) {
        startDateInput = startDate
    }

    fun onStartDateFocusLost() {
        // TODO ...
    }

    fun onEndDateChanged(endDate: LocalDateInput?) {
        endDateInput = endDate
    }

    fun onEndDateFocusLost() {
        // TODO ...
    }
}
