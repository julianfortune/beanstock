package com.julianfortune.beanstock.ui.page.delivery

import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.common.layout.ListDetailScaffold
import com.julianfortune.beanstock.ui.feature.delivery.detail.DeliveryDetail
import com.julianfortune.beanstock.ui.feature.delivery.list.DeliveryHeadlineList


@Composable
fun DeliveriesPage() {
    ListDetailScaffold(
        listWidth = 320.dp,
        listView = {
            DeliveryHeadlineList()
        },
        separator = {
            VerticalDivider(thickness = 1.dp)
        },
    ) {
        DeliveryDetail()
    }
}
