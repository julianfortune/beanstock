package com.julianfortune.beanstock.ui.feature.savedweight.form

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.julianfortune.beanstock.data.model.Weight
import com.julianfortune.beanstock.ui.common.data.FormFieldState
import com.julianfortune.beanstock.ui.feature.savedweight.form.data.SavedWeightBody
import com.julianfortune.beanstock.ui.feature.savedweight.form.data.SavedWeightFormState

class SavedWeightStateHolder {

    private var poundsInput by mutableStateOf("")
    private var ouncesInput by mutableStateOf("")

    val validData: SavedWeightBody? by derivedStateOf {
        val pounds = poundsInput.toIntOrNull()
        val ounces = ouncesInput.toFloatOrNull()

        if (listOf(pounds, ounces).any { it != null }
            && (pounds ?: 0) >= 0
            && (ounces ?: 0f) >= 0f
        ) {
            SavedWeightBody(Weight.ofImperial(pounds ?: 0, ounces ?: 0f))
        } else {
            null
        }
    }

    val uiState: SavedWeightFormState by derivedStateOf {
        SavedWeightFormState(
            FormFieldState(poundsInput),
            FormFieldState(ouncesInput),
            isValid = validData != null
        )
    }

    fun onPoundsChange(value: String) {
        poundsInput = value
    }

    fun onOuncesChange(value: String) {
        ouncesInput = value
    }

    fun clear() {
        poundsInput = ""
        ouncesInput = ""
    }
}
