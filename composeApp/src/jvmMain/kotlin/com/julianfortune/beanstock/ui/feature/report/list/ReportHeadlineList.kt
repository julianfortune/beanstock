package com.julianfortune.beanstock.ui.feature.report.list


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.common.formatLocalDate
import com.julianfortune.beanstock.ui.common.foundation.Dialog
import com.julianfortune.beanstock.ui.common.layout.Collection
import com.julianfortune.beanstock.ui.feature.report.list.ui.CreateReportForm
import org.koin.compose.viewmodel.koinViewModel
import java.time.format.FormatStyle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportHeadlineList(
    viewModel: ReportHeadlineListViewModel = koinViewModel(),
) {
    val selectedId by viewModel.selectedId.collectAsState(null)
    val reportHeadlines by viewModel.allReports.collectAsState(emptyList())

    var creationDialogIsOpen by remember { mutableStateOf(false) }

    Collection(
        "Reports",
        reportHeadlines,
        selectedId,
        content = { report, modifier, elevation ->
            val startString = formatLocalDate(report.start, FormatStyle.SHORT)
            val endString = formatLocalDate(report.end, FormatStyle.SHORT)

            // Prevent re-draws when inserting a new report at the top
            key(report.id) {
                ListItem(
                    headlineContent = {
                        Text(report.name)
                    },
                    supportingContent = {
                        Text("$startString to $endString")
                    },
                    modifier = modifier.clickable(
                        enabled = true,
                        onClick = {
                            viewModel.onSelect(report.id)
                        }
                    ),
                    tonalElevation = elevation,
                )
            }
        },
        onClickCreateNew = {
            creationDialogIsOpen = true
        }
    )

    if (creationDialogIsOpen) {
        Dialog(
            onDismissRequest = { creationDialogIsOpen = false },
        ) {
            CreateReportForm(
                onCancel = {
                    creationDialogIsOpen = false
                },
                onSubmit = { newReport ->
                    viewModel.createNewReport(newReport)
                    creationDialogIsOpen = false
                },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
