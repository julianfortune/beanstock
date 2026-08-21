package com.julianfortune.beanstock.ui.feature.entry.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.beanstock.data.model.CostStatus
import com.julianfortune.beanstock.data.model.Item
import com.julianfortune.beanstock.data.model.Weight
import com.julianfortune.beanstock.data.repository.ItemRepository
import com.julianfortune.beanstock.domain.ItemAutocompleteUseCase
import com.julianfortune.beanstock.ui.common.data.Dynamic
import com.julianfortune.beanstock.ui.common.data.Option
import com.julianfortune.beanstock.ui.common.formatWeight
import com.julianfortune.beanstock.ui.common.input.CurrencyInput
import com.julianfortune.beanstock.ui.delegate.AccountOptionsProvider
import com.julianfortune.beanstock.ui.delegate.ProgramOptionsProvider
import com.julianfortune.beanstock.ui.feature.entry.form.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class EntryFormViewModel(
    private val itemRepository: ItemRepository,
    private val itemAutocompleteUseCase: ItemAutocompleteUseCase,
    private val programOptionsProvider: ProgramOptionsProvider,
    private val accountOptionsProvider: AccountOptionsProvider,
) : ViewModel(),
    // TODO: Should all be use-cases
    ProgramOptionsProvider by programOptionsProvider,
    AccountOptionsProvider by accountOptionsProvider {

    private data class FormInputs(
        val selectedItemId: Long? = null,
        // TODO: Just make this a `Weight?` or Long? holding centigrams
        val weightIndex: Int? = null,
        val unitPoundsInput: String = "",
        val unitOuncesInput: String = "",
        val itemCountInput: String = "",
        val costStatusIsNoCost: Boolean = false,
        val unitCostInput: CurrencyInput? = null,
        val unitCountInput: String = "",
        val selectedProgramId: Long? = null,
        val selectedAccountId: Long? = null,
    )

    private val _inputs = MutableStateFlow(FormInputs())

    private val _currentItem = _inputs
        .map { it.selectedItemId }
        .distinctUntilChanged()
        .flatMapLatest { itemId ->
            when (itemId) {
                null -> flowOf(null)
                else -> itemRepository.getItemById(itemId)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily, // Waits for a subscriber and then never stops
            initialValue = null
        )

    private val _isEditing = MutableStateFlow(false)

    private val _weightOptions: Flow<ItemWeightOptionsState> = combine(
        _inputs,
        _currentItem,
    ) { inputs, currentItem ->
        when {
            inputs.selectedItemId == null -> ItemWeightOptionsState.Disabled
            currentItem == null || inputs.selectedItemId != currentItem.id -> ItemWeightOptionsState.Loading
            currentItem.format is Item.Format.Packaged -> {
                ItemWeightOptionsState.Enabled(
                    currentItem.format.sizes.sortedBy { it.centigrams }.mapIndexed { index, weight ->
                        val name = formatWeight(weight)

                        Option(index, name)
                    }
                )
            }

            else -> ItemWeightOptionsState.Disabled
        }
    }

    private val _validData: Flow<EntryBody?> = combine(
        _inputs,
        _currentItem,
    ) { inputs, currentItem ->
        // An item must be selected
        val itemId = inputs.selectedItemId ?: return@combine null

        // A unit count must be provided
        val unitCount = inputs.unitCountInput.toLongOrNull()
        if (unitCount == null || unitCount <= 0) return@combine null

        // Only defined if the user has selected a package size from the dropdown
        val itemWeight: Weight? = inputs.weightIndex?.let { selectedWeightIndex ->
            (currentItem?.format as? Item.Format.Packaged)?.sizes?.sortedBy { it.centigrams }[selectedWeightIndex]
        }
        // Items per unit should only be considered when an item weight is defined
        val itemsPerUnit: Long? = when {
            itemWeight != null -> inputs.itemCountInput.toLongOrNull()
            else -> null
        }

        val unitWeight = when {
            // When an item packaging weight is selected, use the count of items per unit to compute the unit weight
            itemWeight != null -> {
                if (itemsPerUnit == null) return@combine null

                itemWeight.times(itemsPerUnit)
            }

            // Otherwise, read the unit weight directly from the unit weight inputs
            else -> {
                val pounds: Int? = inputs.unitPoundsInput.toIntOrNull()
                val ounces: Float = inputs.unitOuncesInput.toFloatOrNull() ?: 0f

                if (pounds == null || pounds < 0 || ounces < 0f) return@combine null

                Weight.ofImperial(pounds, ounces)
            }
        }

        val costStatus = when {
            inputs.costStatusIsNoCost -> CostStatus.NO_COST
            else -> CostStatus.PURCHASED
        }

        // A unit cost must be provided when not 'no-cost'
        val unitCostCents = when (costStatus) {
            CostStatus.PURCHASED -> inputs.unitCostInput?.toLong()
            CostStatus.NO_COST -> 0L
        }
        if (unitCostCents == null) return@combine null

        EntryBody(
            itemId,
            unitCount,
            unitWeight,
            costStatus,
            unitCostCents,
            itemWeight,
            itemsPerUnit,
            inputs.selectedProgramId,
            inputs.selectedAccountId,
        )
    }

    val validData: StateFlow<EntryBody?> = _validData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val uiState: StateFlow<EntryFormState> = combine(
        _inputs,
        _currentItem,
        _weightOptions,
        _isEditing,
        _validData
    ) { inputs, loadedItem, weightOptions, isEditing, validData ->
        val unitWeight = when (inputs.weightIndex) {
            null -> UnitWeightState.LooseItems(inputs.unitPoundsInput, inputs.unitOuncesInput)
            else -> UnitWeightState.PackagedItems(inputs.itemCountInput)
        }
        val itemOption = when {
            inputs.selectedItemId == null -> Dynamic.Present(null)
            loadedItem == null || inputs.selectedItemId != loadedItem.id -> Dynamic.Loading
            else -> Dynamic.Present(Option(loadedItem.id, loadedItem.name))
        }

        EntryFormState(
            title = if (isEditing) "Edit Entry" else "New Entry",
            submissionText = if (isEditing) "Save" else "Create",
            selectedItem = itemOption,
            itemWeightOptions = weightOptions,
            selectedItemWeightIndex = inputs.weightIndex,
            unitWeight = unitWeight,
            costStatusIsNoCost = inputs.costStatusIsNoCost,
            unitCost = inputs.unitCostInput?.value ?: "",
            unitCount = inputs.unitCountInput,
            selectedProgramId = inputs.selectedProgramId,
            selectedAccountId = inputs.selectedAccountId,
            isValid = validData != null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EntryFormState() // Provide an initial loading/empty state
    )

    fun getItemOptionsForQuery(query: String?): Flow<List<Option<Long>>> {
        return itemAutocompleteUseCase(query)
    }

    fun onCreateNewItem(name: String) {
        viewModelScope.launch {
            val id = itemRepository.insert(name).getOrThrow() // TODO: Error handling

            _inputs.update {
                it.copy(
                    selectedItemId = id,
                    weightIndex = null,
                )
            }
        }
    }

    fun setInitialEntry(entryBody: EntryBody?) {
        _isEditing.value = entryBody != null

        when (entryBody) {
            null -> _inputs.value = FormInputs()
            else -> {
                val (lbs, oz) = entryBody.unitWeight.toImperial()

                _inputs.value = FormInputs(
                    selectedItemId = entryBody.itemId,
                    weightIndex = null,
                    unitPoundsInput = if (lbs != 0L) lbs.toString() else "",
                    unitOuncesInput = if (oz > 0) oz.toString().removeSuffix(".0") else "",
                    itemCountInput = entryBody.itemsPerUnit?.toString() ?: "",
                    costStatusIsNoCost = entryBody.costStatus == CostStatus.NO_COST,
                    unitCostInput = CurrencyInput.fromLong(entryBody.unitCostCents),
                    unitCountInput = "",
                    selectedProgramId = entryBody.programId,
                    selectedAccountId = entryBody.accountId
                )

                viewModelScope.launch {
                    // Wait for the referenced item to be loaded
                    val item = _currentItem.filterNotNull().first { it.id == entryBody.itemId }

                    val weightIndex = when (item.format) {
                        is Item.Format.Packaged -> item.format.sizes.indexOfFirst { it.centigrams == entryBody.itemWeight?.centigrams }
                            .takeIf { it != -1 }

                        else -> null
                    }

                    if (weightIndex != null) {
                        _inputs.update { currentInputs ->
                            currentInputs.copy(
                                weightIndex = weightIndex,
                                unitCountInput = entryBody.unitCount.toString()
                            )
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: EntryFormEvent) {
        when (event) {
            is EntryFormEvent.AccountSelected -> {
                _inputs.update { it.copy(selectedAccountId = event.accountId) }
            }

            is EntryFormEvent.CostStatusChanged -> {
                _inputs.update { it.copy(costStatusIsNoCost = event.isNoCost) }
            }

            is EntryFormEvent.ItemCountChanged -> {
                _inputs.update { it.copy(itemCountInput = event.value) }
            }

            is EntryFormEvent.ItemSelected -> {
                _inputs.update {
                    it.copy(
                        selectedItemId = event.itemId,
                        weightIndex = null,
                    )
                }
            }

            is EntryFormEvent.ItemWeightSelected -> {
                _inputs.update { it.copy(weightIndex = event.index) }
            }

            is EntryFormEvent.ProgramSelected -> {
                _inputs.update { it.copy(selectedProgramId = event.programId) }
            }

            is EntryFormEvent.UnitCostChanged -> {
                _inputs.update { it.copy(unitCostInput = event.value) }
            }

            is EntryFormEvent.UnitCostLostFocus -> {
                _inputs.update { it.copy(unitCostInput = it.unitCostInput?.toSimplifiedForm()) }
            }

            is EntryFormEvent.UnitCountChanged -> {
                _inputs.update { it.copy(unitCountInput = event.value) }
            }

            is EntryFormEvent.WeightOuncesChanged -> {
                _inputs.update { it.copy(unitOuncesInput = event.value) }
            }

            is EntryFormEvent.WeightPoundsChanged -> {
                _inputs.update { it.copy(unitPoundsInput = event.value) }
            }

            EntryFormEvent.AbortForm -> Unit
            EntryFormEvent.SubmitForm -> Unit
        }
    }

}
