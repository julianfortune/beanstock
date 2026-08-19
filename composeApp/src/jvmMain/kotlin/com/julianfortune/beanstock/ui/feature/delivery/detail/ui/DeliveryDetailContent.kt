package com.julianfortune.beanstock.ui.feature.delivery.detail.ui


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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.common.component.KeyValueItem
import com.julianfortune.beanstock.ui.common.component.KeyValueList

private val horizontalContentPadding = 24.dp
private val horizontalTextPadding = 16.dp


@Composable
fun DeliveryHeader(
    receivedDate: String,
    supplierName: String,
    taxes: String,
    fees: String,
    onClickEdit: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = horizontalContentPadding),
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
                modifier = Modifier.padding(vertical = 8.dp, horizontal = horizontalTextPadding)
            )

            Row {
                IconButton(
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    onClick = onClickEdit
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit details"
                    )
                }
            }
        }

        KeyValueList(
            modifier = Modifier.widthIn(max = 360.dp).padding(horizontal = horizontalTextPadding)
        ) {
            KeyValueItem(Icons.Outlined.CalendarMonth, "Received", receivedDate)
            KeyValueItem(Icons.Outlined.Storefront, "Supplier", supplierName)
            KeyValueItem(Icons.Outlined.Gavel, "Taxes", taxes, FontFamily.Monospace)
            KeyValueItem(Icons.Outlined.CreditCard, "Fees", fees, FontFamily.Monospace)
        }
    }
}

@Composable
fun DeliverySummary(
    subtotal: String,
    taxes: String,
    fees: String,
    total: String,
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = horizontalContentPadding)) {
        Text(
            text = "Summary",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp, top = 24.dp, 16.dp, bottom = 8.dp)
        )
        Spacer(Modifier.height(12.dp))

        KeyValueList(
            modifier = Modifier
                .widthIn(max = 360.dp)
                .padding(horizontal = horizontalTextPadding)
        ) {
            KeyValueItem(
                Icons.Outlined.Summarize, "Subtotal", subtotal, FontFamily.Monospace
            )
            KeyValueItem(
                Icons.Outlined.Gavel, "Taxes", taxes, FontFamily.Monospace
            )
            KeyValueItem(
                Icons.Outlined.CreditCard, "Fees", fees, FontFamily.Monospace
            )
            KeyValueItem(
                Icons.Outlined.Money, "Total", total, FontFamily.Monospace
            )

        }
    }
}

//@Preview
//@Composable
//fun DeliveryPageContentPreview() {
//    AppPreview {
//        DeliveryPageContent(
//            DeliveryContentState(
//                "09/10/2998",
//                "ABC Foods",
//                0,
//                ToggleableState.Off,
//                listOf(
//                    EntryRowState(1, false, "Green Beans", null, null, "4", "40.0", "$28.00"),
//                    EntryRowState(2, false, "Lettuce", null, null, "7", "70.0", "$43.00"),
//                ),
//                "11",
//                "110.0",
//                "$800.00",
//                "$0.00",
//                "$0.00",
//                "$800.00",
//            ),
//            {},
//        )
//    }
//}
//
//@Preview
//@Composable
//fun DeliveryPageContentMaximalistPreview() {
//    AppPreview {
//        DeliveryPageContent(
//            DeliveryContentState(
//                "12/31/2000",
//                "Abracadabra Foods Incorporated",
//                0,
//                ToggleableState.Off,
//                listOf(
//                    EntryRowState(1, false, "Organic Himalayan Salt", null, null, "100", "4000.0", "$2800.00"),
//                    EntryRowState(2, false, "Organic Fresh Dinosaur Kale", null, null, "7", "70.0", "$43.00"),
//                ),
//                "3000",
//                "10320.0",
//                "$11000.00",
//                "$999.00",
//                "$999.00",
//                "$99999.99",
//            ),
//            {},
//        )
//    }
//}
