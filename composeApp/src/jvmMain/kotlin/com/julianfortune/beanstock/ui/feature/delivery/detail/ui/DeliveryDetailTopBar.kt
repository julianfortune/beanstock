package com.julianfortune.beanstock.ui.feature.delivery.detail.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.tooling.preview.Preview
import com.julianfortune.beanstock.ui.common.component.TopBar
import com.julianfortune.beanstock.ui.theme.AppPreview


@Composable
fun DeliveryDetailTopBar(
    title: String,
    onClickDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopBar(title, modifier) {
        Row {
            IconButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = onClickDelete
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete delivery"
                )
            }
        }
    }
}


@Preview
@Composable
fun DeliveryPageTopBarPreview() = AppPreview {
    DeliveryDetailTopBar(title = "Delivery title", onClickDelete = {})
}
