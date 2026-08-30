package com.julianfortune.beanstock.ui.feature.report.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.data.model.*
import com.julianfortune.beanstock.ui.common.component.ConfirmDeleteEntityForm
import com.julianfortune.beanstock.ui.common.component.TopBar
import com.julianfortune.beanstock.ui.common.foundation.Dialog
import com.julianfortune.beanstock.ui.common.foundation.ScrollableColumn
import com.julianfortune.beanstock.ui.common.foundation.SideSheet
import com.julianfortune.beanstock.ui.feature.report.detail.data.ReportCriteriaState
import com.julianfortune.beanstock.ui.feature.report.detail.data.ReportDetailState
import com.julianfortune.beanstock.ui.feature.report.detail.data.ReportNameBody
import com.julianfortune.beanstock.ui.feature.report.detail.data.ReportResultState
import com.julianfortune.beanstock.ui.feature.report.detail.ui.EditReportCriteriaForm
import com.julianfortune.beanstock.ui.feature.report.detail.ui.ReportCriteriaSection
import com.julianfortune.beanstock.ui.feature.report.detail.ui.ReportResultSection
import com.julianfortune.beanstock.ui.theme.AppPreview
import org.koin.compose.viewmodel.koinViewModel


private val contentMaxWidth = 960.dp

@Composable
fun ReportDetail(
    viewModel: ReportDetailViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    val itemOptions by viewModel.itemOptions.collectAsState(emptyList())
    val categoryOptions by viewModel.categoryOptions.collectAsState(emptyList())
    val programOptions by viewModel.programOptions.collectAsState(emptyList())
    val accountOptions by viewModel.accountOptions.collectAsState(emptyList())
    val supplierOptions by viewModel.supplierOptions.collectAsState(emptyList())

    var deleteDialogIsOpen by remember { mutableStateOf(false) }

    ReportDetailUi(
        state = state,
        onClickEditName = viewModel::onEditName,
        onClickEditCriteria = viewModel::onEditCriteria,
        onClickDelete = {
            deleteDialogIsOpen = true
        }
    )

    SideSheet(
        state = (state as? ReportDetailState.Success)?.editCriteria,
        onDismissRequest = { viewModel.onCancelEditCriteria() },
        modifier = Modifier.width(640.dp),
    ) { editCriteria ->
        EditReportCriteriaForm(
            editCriteria,
            itemOptions,
            categoryOptions,
            programOptions,
            accountOptions,
            supplierOptions,
            onCancel = {
                viewModel.onCancelEditCriteria()
            },
            onSubmit = { newCriteria ->
                viewModel.onSubmitEditCriteria(newCriteria)
            },
            modifier = Modifier.padding(16.dp)
        )
    }

    (state as? ReportDetailState.Success)?.editName?.let { existingName ->
        Dialog(
            onDismissRequest = viewModel::onCancelEditName,
        ) {
            EditReportCriteriaForm(
                ReportNameBody(existingName),
                onCancel = viewModel::onCancelEditName,
                onSubmit = { newName ->
                    viewModel.onSubmitEditName(newName.name)
                },
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    if (deleteDialogIsOpen) {
        Dialog(
            onDismissRequest = { deleteDialogIsOpen = false },
        ) {
            ConfirmDeleteEntityForm(
                "Delete Report",
                onCancel = {
                    deleteDialogIsOpen = false
                },
                onConfirm = {
                    viewModel.onDeleteReport()
                    deleteDialogIsOpen = false
                }
            )
        }
    }
}

@Composable
fun ReportDetailUi(
    state: ReportDetailState,
    onClickDelete: () -> Unit = {},
    onClickEditName: () -> Unit = {},
    onClickEditCriteria: () -> Unit = {},
) {
    val title = when (state) {
        is ReportDetailState.Success -> state.name
        is ReportDetailState.Loading -> "Loading"
        is ReportDetailState.Empty -> ""
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(title) {
            if (state is ReportDetailState.Success) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).height(24.dp),
                        onClick = onClickEditName
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Rename report",
                            modifier = Modifier.height(16.dp)
                        )
                    }

                    IconButton(
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                        onClick = onClickDelete
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete report"
                        )
                    }
                }
            }
        }

        ScrollableColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Column(
                    modifier = Modifier.width(contentMaxWidth).padding(vertical = 32.dp, horizontal = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    (state as? ReportDetailState.Success)?.criteria?.let { criteriaState ->
                        ReportCriteriaSection(
                            state = criteriaState,
                            onClickEdit = onClickEditCriteria
                        )
                    }

                    (state as? ReportDetailState.Success)?.results?.let { resultState ->
                        ReportResultSection(
                            resultState,
                            Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun ReportDetailUiPreview() = AppPreview {
    ReportDetailUi(
        state = ReportDetailState.Success(
            1,
            "Report 2019",
            ReportCriteriaState(
                startDate = "01/01/2019",
                endDate = "01/31/2019",
                item = ItemHeadline(1, "Carrot"),
                category = Category(1, "Produce"),
                costStatus = CostStatus.PURCHASED,
                program = Program(1, "Breakfast"),
                account = Account(1, "Account I"),
                supplier = Supplier(1, "ABC Foods"),
            ),
            ReportResultState(
                2,
                8,
                "80.0",
                "$120.00",
                "$0.00",
                "$0.00"
            ),
        )
    )
}
