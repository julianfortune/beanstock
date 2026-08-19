package com.julianfortune.beanstock.ui.feature.report.detail.data

import com.julianfortune.beanstock.ui.common.data.FormFieldState

data class EditReportCriteriaFormState(
    val startDate: FormFieldState<String> = FormFieldState(""),
    val endDate: FormFieldState<String> = FormFieldState(""),
    val itemId: FormFieldState<Long?> = FormFieldState(null),
    val categoryId: FormFieldState<Long?> = FormFieldState(null),
    val costStatusIsNoCost: FormFieldState<Boolean?> = FormFieldState(null),
    val programId: FormFieldState<Long?> = FormFieldState(null),
    val accountId: FormFieldState<Long?> = FormFieldState(null),
    val supplierId: FormFieldState<Long?> = FormFieldState(null),
    val isValid: Boolean = false,
)
