package com.julianfortune.beanstock

import androidx.compose.foundation.DarkDefaultContextMenuRepresentation
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Cases
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.EggAlt
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.data.model.Account
import com.julianfortune.beanstock.data.model.Category
import com.julianfortune.beanstock.data.model.Program
import com.julianfortune.beanstock.data.model.Supplier
import com.julianfortune.beanstock.ui.page.delivery.DeliveriesPage
import com.julianfortune.beanstock.ui.page.item.ItemsPage
import com.julianfortune.beanstock.ui.page.namedentity.NamedEntityPage
import com.julianfortune.beanstock.ui.page.report.ReportsPage
import com.julianfortune.beanstock.ui.theme.AppTypography
import com.julianfortune.beanstock.ui.theme.darkScheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

enum class NavigationPage(val title: String, val icon: ImageVector) {
    DELIVERIES("Deliveries", Icons.Outlined.LocalShipping),
    ITEMS("Items", Icons.Outlined.EggAlt),
    SUPPLIERS("Suppliers", Icons.Outlined.Storefront),
    PROGRAMS("Programs", Icons.Outlined.Cases),
    PURCHASING_ACCOUNTS("Accounts", Icons.Outlined.AccountBalanceWallet),
    CATEGORIES("Categories", Icons.Outlined.Category),
    REPORTS("Reports", Icons.Outlined.Analytics),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    var selectedNavigationItem by remember { mutableStateOf(NavigationPage.DELIVERIES) }

    MaterialTheme(
        colorScheme = darkScheme, //  darkColorScheme() or lightColorScheme()
        typography = AppTypography(),
    ) {
        val contextMenuRepresentation = DarkDefaultContextMenuRepresentation // LightDefaultContextMenuRepresentation
        CompositionLocalProvider(LocalContextMenuRepresentation provides contextMenuRepresentation) {
            Row {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        NavigationPage.entries.forEach { page ->
                            NavigationRailItem(
                                colors = NavigationRailItemDefaults.colors(),
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                                selected = selectedNavigationItem == page,
                                onClick = {
                                    selectedNavigationItem = page
                                },
                                icon = {
                                    Icon(page.icon, null)
                                },
                                label = {
                                    Text(
                                        page.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                })
                        }
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (selectedNavigationItem) {
                        NavigationPage.CATEGORIES -> NamedEntityPage<Category>(
                            koinViewModel(named("categoryViewModel")),
                            "Categories",
                            "Category"
                        )

                        NavigationPage.DELIVERIES -> DeliveriesPage()

                        NavigationPage.ITEMS -> ItemsPage()

                        NavigationPage.PROGRAMS -> NamedEntityPage<com.julianfortune.beanstock.data.model.Program>(
                            koinViewModel(named("programViewModel")),
                            "Programs",
                            "Program"
                        )

                        NavigationPage.PURCHASING_ACCOUNTS -> NamedEntityPage<com.julianfortune.beanstock.data.model.Account>(
                            koinViewModel(named("purchasingAccountViewModel")),
                            "Accounts",
                            "Account"
                        )

                        NavigationPage.SUPPLIERS -> NamedEntityPage<com.julianfortune.beanstock.data.model.Supplier>(
                            koinViewModel(named("supplierViewModel")),
                            "Suppliers",
                            "Supplier"
                        )

                        NavigationPage.REPORTS -> ReportsPage()
                    }
                }
            }
        }
    }
}