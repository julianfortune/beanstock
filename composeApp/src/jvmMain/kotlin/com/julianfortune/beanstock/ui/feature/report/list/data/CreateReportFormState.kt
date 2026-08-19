package com.julianfortune.beanstock.ui.feature.report.list.data

import com.julianfortune.beanstock.ui.common.data.FormFieldState

data class CreateReportFormState(
    val name: FormFieldState<String> = FormFieldState(""),
    val startDate: FormFieldState<String> = FormFieldState(""),
    val endDate: FormFieldState<String> = FormFieldState(""),
    val isValid: Boolean = false,
)
