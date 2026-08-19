package com.julianfortune.glacier.ui.common.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.julianfortune.glacier.ui.theme.AppPreview


@Composable
fun KeyValueList(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        modifier = modifier,
    ) {
        content()
    }
}

@Composable
fun KeyValueItem(
    icon: ImageVector,
    key: String,
    value: String,
    valueFontStyle: FontFamily = FontFamily.Default,
    hint: String? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Icon(
            icon,
            key,
            modifier = Modifier.height(14.dp),
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        Spacer(Modifier.width(6.dp))

        Text(
            key,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        hint?.let {
            Spacer(Modifier.width(12.dp))
            KeyHint(hint)
            Spacer(Modifier.width(4.dp))
        }

        SelectionContainer(modifier = Modifier.weight(1f)) {
            Text(
                value,
                textAlign = TextAlign.End,
                fontFamily = valueFontStyle,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyHint(hint: String) {
    var popoverIsOpen by remember { mutableStateOf(false) }

    Column {
        IconButton(
            onClick = { popoverIsOpen = !popoverIsOpen },
            modifier = Modifier
                .height(16.dp)
                .width(16.dp)
                .pointerHoverIcon(PointerIcon.Hand),
        ) {
            Icon(Icons.Outlined.Info, "Info")
        }

        // TODO(P3): Try to fix alignment
        Box {
            if (popoverIsOpen) {
                Popup(
                    alignment = Alignment.TopCenter,
                    // Dismisses the popup when clicking outside
                    onDismissRequest = { popoverIsOpen = false },
                    properties = PopupProperties(focusable = true)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        OutlinedCard(
                            colors = CardDefaults.cardColors().copy(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            ),
                            modifier = Modifier.widthIn(max = 400.dp)
                        ) {
                            Text(
                                hint,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun KeyValueListPreview() {
    AppPreview {
        KeyValueList(
            modifier = Modifier.padding(32.dp)
        ) {
            KeyValueItem(Icons.Outlined.Storefront, "Supplier", "ABC Foods")
            KeyValueItem(Icons.Outlined.Gavel, "Taxes", "$10.00", FontFamily.Monospace, "Testing")
            KeyValueItem(Icons.Outlined.CreditCard, "Fees", "$15.00", FontFamily.Monospace)
        }
    }
}