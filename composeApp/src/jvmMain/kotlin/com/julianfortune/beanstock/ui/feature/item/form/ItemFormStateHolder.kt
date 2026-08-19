package com.julianfortune.beanstock.ui.page.item.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.julianfortune.beanstock.data.model.Item
import com.julianfortune.beanstock.data.model.Weight
import com.julianfortune.beanstock.ui.common.data.FormFieldState
import com.julianfortune.beanstock.ui.common.formatWeight
import com.julianfortune.beanstock.ui.feature.item.data.ItemBody
import com.julianfortune.beanstock.ui.feature.item.data.ItemFormState
import com.julianfortune.beanstock.ui.feature.item.data.ItemFormatState


sealed interface FormatInput {
    data object Loose : FormatInput
    data class Packaged(val sizes: Set<Weight>) : FormatInput
}

class ItemFormStateHolder(initialValue: ItemBody? = null) {

    private var nameInput by mutableStateOf(initialValue?.name ?: "")
    private var categoryIdInput by mutableStateOf(initialValue?.categoryId)
    private var formatInput by mutableStateOf(
        (initialValue?.format as? Item.Format.Packaged)?.let {
            FormatInput.Packaged(it.sizes)
        } ?: FormatInput.Loose
    )

    val validData: ItemBody? by derivedStateOf {
        val currentName = nameInput
        val currentCategoryId = categoryIdInput

        val currentPackagingInput = formatInput
        val packagingIsValid = when (currentPackagingInput) {
            is FormatInput.Loose -> true
            is FormatInput.Packaged -> currentPackagingInput.sizes.isNotEmpty()
        }

        if (currentName != "" && packagingIsValid) {
            val format = when (currentPackagingInput) {
                is FormatInput.Loose -> Item.Format.Loose
                is FormatInput.Packaged -> Item.Format.Packaged(currentPackagingInput.sizes)
            }

            ItemBody(
                currentName,
                currentCategoryId,
                format,
            )
        } else {
            null
        }
    }

    val uiState: ItemFormState by derivedStateOf {
        val format = when (val currentPackagingInput = formatInput) {
            is FormatInput.Loose -> ItemFormatState.Loose
            is FormatInput.Packaged -> {
                val sizes = currentPackagingInput.sizes
                    .sortedBy { it.centigrams }
                    .map { formatWeight(it) }

                ItemFormatState.Packaged(sizes)
            }
        }

        ItemFormState(
            FormFieldState(nameInput),
            FormFieldState(categoryIdInput),
            format,
            isValid = validData != null
        )
    }

    fun onNameChange(newValue: String) {
        nameInput = newValue
    }

    fun onCategoryIdChange(newValue: Long?) {
        categoryIdInput = newValue
    }

    fun onPackagingIsLooseChange(newValue: Boolean) {
        val currentPackingIsLoose = formatInput is FormatInput.Loose
        // Ensure we only switch the state if needed in order to not reset the packaged sizes
        if (currentPackingIsLoose != newValue) {
            formatInput = when (newValue) {
                true -> FormatInput.Loose
                false -> FormatInput.Packaged(emptySet())
            }
        }
    }

    fun onAddDiscretePackageSize(newSize: Weight) {
        when (val current = formatInput) {
            is FormatInput.Packaged -> {
                formatInput = current.copy(
                    sizes = current.sizes.plus(newSize)
                )
            }

            else -> Unit
        }
    }

    fun onRemoveDiscretePackageSize(index: Int) {
        when (val current = formatInput) {
            is FormatInput.Packaged -> {
                formatInput = current.copy(
                    sizes = current.sizes.sortedBy { it.centigrams }.filterIndexed { i, _ -> i != index }.toSet()
                )
            }

            else -> Unit
        }
    }
}
