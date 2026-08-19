package com.julianfortune.beanstock.ui.feature.report.detail.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.data.model.*
import com.julianfortune.beanstock.ui.common.component.KeyValueItem
import com.julianfortune.beanstock.ui.common.component.KeyValueList
import com.julianfortune.beanstock.ui.feature.report.detail.data.ReportCriteriaState
import com.julianfortune.beanstock.ui.theme.AppPreview

@Composable
fun ReportCriteriaSection(
    state: ReportCriteriaState,
    onClickEdit: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Criteria",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row {
                IconButton(
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    onClick = onClickEdit
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit criteria"
                    )
                }
            }
        }

        KeyValueList(
            modifier = Modifier.widthIn(max = 360.dp)
        ) {
            KeyValueItem(Icons.Outlined.CalendarMonth, "Start", state.startDate)
            KeyValueItem(Icons.Outlined.CalendarMonth, "End", state.endDate)

            state.item?.let { KeyValueItem(Icons.Outlined.EggAlt, "Item", it.name) }
            state.category?.let { KeyValueItem(Icons.Outlined.Category, "Category", it.name) }
            state.costStatus?.let {
                val description = when (it) {
                    CostStatus.PURCHASED -> "Paid"
                    CostStatus.NO_COST -> "No cost"
                }
                KeyValueItem(Icons.Outlined.Money, "Cost Status", description)
            }
            state.program?.let { KeyValueItem(Icons.Outlined.Cases, "Program", it.name) }
            state.account?.let { KeyValueItem(Icons.Outlined.AccountBalanceWallet, "Account", it.name) }
            state.supplier?.let { KeyValueItem(Icons.Outlined.Storefront, "Supplier", it.name) }
        }
    }
}

@Composable
@Preview
fun ReportCriteriaSectionPreview() = AppPreview {
    ReportCriteriaSection(
        ReportCriteriaState(
            startDate = "01/01/2019",
            endDate = "01/31/2019",
            item = ItemHeadline(1, "Carrot"),
            category = Category(1, "Produce"),
            costStatus = CostStatus.PURCHASED,
            program = Program(1, "Breakfast"),
            account = Account(1, "Account I"),
            supplier = Supplier(1, "ABC Foods"),
        )
    )
}
