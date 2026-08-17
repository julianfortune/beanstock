package com.julianfortune.glacier

import androidx.compose.foundation.DarkDefaultContextMenuRepresentation
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.model.Account
import com.julianfortune.glacier.data.model.Category
import com.julianfortune.glacier.data.model.Program
import com.julianfortune.glacier.data.model.Supplier
import com.julianfortune.glacier.ui.page.delivery.DeliveriesPage
import com.julianfortune.glacier.ui.page.item.ItemsPage
import com.julianfortune.glacier.ui.page.namedentity.NamedEntityPage
import com.julianfortune.glacier.ui.page.report.ReportsPage
import com.julianfortune.glacier.ui.theme.AppTypography
import com.julianfortune.glacier.ui.theme.darkScheme
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

                        NavigationPage.PROGRAMS -> NamedEntityPage<Program>(
                            koinViewModel(named("programViewModel")),
                            "Programs",
                            "Program"
                        )

                        NavigationPage.PURCHASING_ACCOUNTS -> NamedEntityPage<Account>(
                            koinViewModel(named("purchasingAccountViewModel")),
                            "Accounts",
                            "Account"
                        )

                        NavigationPage.SUPPLIERS -> NamedEntityPage<Supplier>(
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
