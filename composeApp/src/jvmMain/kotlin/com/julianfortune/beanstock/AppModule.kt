package com.julianfortune.beanstock

import com.julianfortune.beanstock.data.repository.*
import com.julianfortune.beanstock.db.Database
import com.julianfortune.beanstock.ui.coordinator.delivery.DefaultDeliveryViewCoordinator
import com.julianfortune.beanstock.ui.coordinator.delivery.DeliveryViewCoordinator
import com.julianfortune.beanstock.ui.coordinator.report.DefaultReportViewCoordinator
import com.julianfortune.beanstock.ui.coordinator.report.ReportViewCoordinator
import com.julianfortune.beanstock.ui.delegate.*
import com.julianfortune.beanstock.ui.feature.delivery.detail.DeliveryDetailViewModel
import com.julianfortune.beanstock.ui.feature.delivery.list.DeliveryHeadlineListViewModel
import com.julianfortune.beanstock.ui.feature.entry.form.EntryFormViewModel
import com.julianfortune.beanstock.ui.feature.entry.table.EntryTableViewModel
import com.julianfortune.beanstock.ui.feature.report.detail.ReportDetailViewModel
import com.julianfortune.beanstock.ui.feature.report.list.ReportHeadlineListViewModel
import com.julianfortune.beanstock.ui.page.item.ItemsPageViewModel
import com.julianfortune.beanstock.ui.page.namedentity.NamedEntityPageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single { Database(get()) }

    // Repositories
    single { ReportRepository(get()) }
    single { DeliveryRepository(get()) }
    single { CategoryRepository(get()) }
    single { ItemRepository(get()) }
    single { ReportResultRepository(get()) }
    single { SupplierRepository(get()) }
    single { ProgramRepository(get()) }
    single { AccountRepository(get()) }

    // View coordinators
    single<DeliveryViewCoordinator> {
        DefaultDeliveryViewCoordinator(
            get(),
            CoroutineScope(Dispatchers.Default),
        )
    }
    single<ReportViewCoordinator> {
        DefaultReportViewCoordinator(
            get(),
            CoroutineScope(Dispatchers.Default),
        )
    }

    // ViewModel delegates
    single<CategoryOptionsProvider> {
        DefaultCategoryOptionsProvider(
            categoryRepository = get(),
            scope = CoroutineScope(Dispatchers.Default)
        )
    }
    single<ItemOptionsProvider> {
        DefaultItemOptionsProvider(
            itemRepository = get(),
            scope = CoroutineScope(Dispatchers.Default)
        )
    }
    single<ProgramOptionsProvider> {
        DefaultProgramOptionsProvider(
            programRepository = get(),
            scope = CoroutineScope(Dispatchers.Default)
        )
    }
    single<AccountOptionsProvider> {
        DefaultAccountOptionsProvider(
            accountRepository = get(),
            scope = CoroutineScope(Dispatchers.Default)
        )
    }
    single<SupplierOptionsProvider> {
        DefaultSupplierOptionsProvider(
            supplierRepository = get(),
            scope = CoroutineScope(Dispatchers.Default)
        )
    }

    // ViewModels
    viewModel(named("categoryViewModel")) {
        NamedEntityPageViewModel(get<CategoryRepository>())
    }
    viewModel {
        EntryTableViewModel(
            get(),
            get(),
            get(),
            get(),
        )
    }
    viewModel {
        DeliveryDetailViewModel(
            get(),
            get(),
            get(),
        )
    }
    viewModel {
        DeliveryHeadlineListViewModel(
            get(),
            get(),
            get(),
        )
    }
    viewModel {
        EntryFormViewModel(
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel {
        ItemsPageViewModel(get(), get())
    }
    viewModel(named("programViewModel")) {
        NamedEntityPageViewModel(get<ProgramRepository>())
    }
    viewModel(named("purchasingAccountViewModel")) {
        NamedEntityPageViewModel(get<AccountRepository>())
    }
    viewModel {
        ReportDetailViewModel(get(), get(), get(), get(), get(), get(), get(), get())
    }
    viewModel {
        ReportHeadlineListViewModel(get(), get())
    }
    viewModel(named("supplierViewModel")) {
        NamedEntityPageViewModel(get<SupplierRepository>())
    }

}
