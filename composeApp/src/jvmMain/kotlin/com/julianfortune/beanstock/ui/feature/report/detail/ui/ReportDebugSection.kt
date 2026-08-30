package com.julianfortune.beanstock.ui.feature.report.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.core.util.formatCents
import com.julianfortune.beanstock.data.model.Delivery
import com.julianfortune.beanstock.ui.feature.delivery.detail.calculateEntryTotalCostCents
import com.julianfortune.beanstock.ui.feature.delivery.detail.calculateEntryTotalWeight
import com.julianfortune.beanstock.ui.feature.entry.table.ui.EntryRow

@Composable
fun ReportDebugSection(
    state: List<Delivery>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Details",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        state.forEach { delivery ->
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(16.dp)
                ) {
                    Text(
                        "${delivery.supplier.name} • ${delivery.received}"
                    )
                }

                delivery.entries.forEach { entry ->
                    val totalWeight = calculateEntryTotalWeight(entry).toPounds().toString()
                    val totalCostCents = "$" + formatCents(calculateEntryTotalCostCents(entry))

                    HorizontalDivider(Modifier.height(1.dp))

                    EntryRow(modifier = Modifier.padding(top = 16.dp, end = 16.dp, bottom = 16.dp)) {
                        ItemNameCell { Text(entry.item.name) }
                        EntryWeightCell { Text(totalWeight, fontFamily = FontFamily.Monospace) }
                        EntryCostCell { Text(totalCostCents, fontFamily = FontFamily.Monospace) }
                    }
                }

                delivery.taxesCents?.takeIf { it > 0 }?.let { taxesCents ->
                    HorizontalDivider(Modifier.height(1.dp))

                    EntryRow(modifier = Modifier.padding(top = 16.dp, end = 16.dp, bottom = 16.dp)) {
                        ItemNameCell { Text(
                            "Taxes",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        ) }
                        EntryCostCell { Text(
                            "$" + formatCents(taxesCents), fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        ) }
                    }
                }

                delivery.feesCents?.takeIf { it > 0 }?.let { feesCents ->
                    HorizontalDivider(Modifier.height(1.dp))

                    EntryRow(modifier = Modifier.padding(top = 16.dp, end = 16.dp, bottom = 16.dp)) {
                        ItemNameCell { Text(
                            "Fees",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        ) }
                        EntryCostCell { Text(
                            "$" + formatCents(feesCents), fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        ) }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
