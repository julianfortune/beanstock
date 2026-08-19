package com.julianfortune.beanstock.ui.page.report

import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.common.layout.ListDetailScaffold
import com.julianfortune.beanstock.ui.feature.report.detail.ReportDetail
import com.julianfortune.beanstock.ui.feature.report.list.ReportHeadlineList

@Composable
fun ReportsPage() {
    ListDetailScaffold(
        listWidth = 320.dp,
        listView = {
            ReportHeadlineList()
        },
        separator = {
            VerticalDivider(thickness = 1.dp)
        },
    ) {
        ReportDetail()
    }
}
