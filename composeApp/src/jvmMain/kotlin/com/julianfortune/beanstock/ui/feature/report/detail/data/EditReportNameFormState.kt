package com.julianfortune.beanstock.ui.feature.report.detail.data

import com.julianfortune.beanstock.ui.common.data.FormFieldState

data class EditReportNameFormState(
    val name: FormFieldState<String> = FormFieldState(""),
    val isValid: Boolean = false,
)
