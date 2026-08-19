package com.julianfortune.beanstock.ui.feature.delivery.detail

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.common.component.ConfirmDeleteEntityForm
import com.julianfortune.beanstock.ui.common.component.TopBar
import com.julianfortune.beanstock.ui.common.foundation.Dialog
import com.julianfortune.beanstock.ui.feature.delivery.detail.data.DeliveryAction
import com.julianfortune.beanstock.ui.feature.delivery.detail.data.DeliveryDetailState
import com.julianfortune.beanstock.ui.feature.delivery.detail.ui.DeliveryDetailTopBar
import com.julianfortune.beanstock.ui.feature.delivery.detail.ui.DeliveryHeader
import com.julianfortune.beanstock.ui.feature.delivery.detail.ui.DeliverySummary
import com.julianfortune.beanstock.ui.feature.delivery.form.DeliveryForm
import com.julianfortune.beanstock.ui.feature.entry.table.EntryTable
import com.julianfortune.beanstock.ui.theme.dynamicScrollbarStyle
import org.koin.compose.viewmodel.koinViewModel

private val contentMaxWidth = 960.dp

@Composable
fun DeliveryDetail(
    viewModel: DeliveryDetailViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    // TODO(!!): Turn these into rich state objects with error information
    val deliveryAction by viewModel.deliveryAction

    val supplierOptions by viewModel.supplierOptions.collectAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        (state as? DeliveryDetailState.Success)?.title?.let { title ->
            DeliveryDetailTopBar(
                title,
                onClickDelete = {
                    viewModel.showDeleteDelivery()
                }
            )
        } ?: TopBar("Loading")

        Box(modifier = Modifier.fillMaxSize()) {
            val scrollState = rememberScrollState()

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = contentMaxWidth)
                        .verticalScroll(scrollState)
                        .padding(vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    (state as? DeliveryDetailState.Success)?.content?.let { contentState ->
                        DeliveryHeader(
                            contentState.receivedDate,
                            contentState.supplierName,
                            contentState.taxes,
                            contentState.fees,
                            onClickEdit = {
                                viewModel.showEditDelivery()
                            },
                        )
                    }

                    EntryTable()

                    (state as? DeliveryDetailState.Success)?.content?.let { contentState ->
                        if (contentState.subtotal != "$0.00") {
                            DeliverySummary(
                                contentState.subtotal,
                                contentState.taxes,
                                contentState.fees,
                                contentState.totalCost,
                            )
                        }
                    }

                }
            }

            VerticalScrollbar(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .padding(2.dp),
                adapter = rememberScrollbarAdapter(scrollState),
                style = dynamicScrollbarStyle(MaterialTheme.colorScheme.onBackground)
            )
        }
    }

    deliveryAction?.let { action ->
        when (action) {
            is DeliveryAction.Edit -> {
                Dialog(
                    onDismissRequest = { viewModel.cancelDeliveryOperation() },
                ) {
                    DeliveryForm(
                        title = "Edit Delivery",
                        supplierOptions = supplierOptions,
                        initialDelivery = action.delivery,
                        onCancel = {
                            viewModel.cancelDeliveryOperation()
                        },
                        onSubmit = { newDelivery ->
                            viewModel.updateDelivery(action.id, newDelivery)
                            viewModel.cancelDeliveryOperation()
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            is DeliveryAction.Delete -> {
                Dialog(
                    onDismissRequest = { viewModel.cancelDeliveryOperation() },
                ) {
                    ConfirmDeleteEntityForm(
                        action.id,
                        "Delete Delivery",
                        onCancel = {
                            viewModel.cancelDeliveryOperation()
                        },
                        onConfirm = { id ->
                            viewModel.deleteDelivery(id)
                            viewModel.cancelDeliveryOperation()
                        }
                    )
                }
            }
        }
    }
}
