package com.julianfortune.beanstock.ui.feature.report.detail.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.common.component.KeyValueItem
import com.julianfortune.beanstock.ui.common.component.KeyValueList
import com.julianfortune.beanstock.ui.feature.report.detail.data.ReportResultState
import com.julianfortune.beanstock.ui.theme.AppPreview


@Composable
fun ReportResultSection(
    state: ReportResultState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Summary",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(Modifier.height(12.dp))

        KeyValueList(
            modifier = Modifier.widthIn(max = 360.dp)
        ) {
            KeyValueItem(
                Icons.Default.Scale,
                "Total Weight",
                state.totalWeight,
                FontFamily.Monospace
            )
            KeyValueItem(
                Icons.Outlined.Summarize,
                "Subtotal",
                state.subtotal,
                FontFamily.Monospace,
                hint = "The total cost of all products—excluding taxes and fees associated with delivery",
            )
            KeyValueItem(
                Icons.Default.LocalShipping,
                "Deliveries",
                "${state.deliveryCount}",
                FontFamily.Monospace
            )
            KeyValueItem(
                Icons.Outlined.Gavel,
                "Taxes",
                state.totalTaxes,
                FontFamily.Monospace,
                hint = "The taxes accrued across all deliveries (${state.deliveryCount})"
            )
            KeyValueItem(
                Icons.Outlined.CreditCard,
                "Fees",
                state.totalFees,
                FontFamily.Monospace,
                hint = "The fees accrued across all deliveries (${state.deliveryCount})"
            )

        }
    }
}

@Composable
@Preview
fun ReportResultSectionPreview() = AppPreview {
    ReportResultSection(
        ReportResultState(
            2,
            8,
            "80.0",
            "$120.00",
            "$0.00",
            "$0.00"
        )
    )
}
