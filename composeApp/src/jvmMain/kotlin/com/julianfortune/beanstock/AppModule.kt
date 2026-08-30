package com.julianfortune.beanstock

import com.julianfortune.beanstock.data.model.Account
import com.julianfortune.beanstock.data.model.Category
import com.julianfortune.beanstock.data.model.Program
import com.julianfortune.beanstock.data.model.Supplier
import com.julianfortune.beanstock.data.repository.*
import com.julianfortune.beanstock.db.Database
import com.julianfortune.beanstock.domain.GetItemOptionsUseCase
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

object Qualifiers {
    val categoryRepository = named("categoryRepository")
    val supplierRepository = named("supplierRepository")
    val programRepository = named("programRepository")
    val accountRepository = named("accountRepository")
}

val appModule = module {
    single { Database(get()) }

    // Repositories
    single { DeliveryRepository(get()) }
    single { ItemRepository(get()) }
    single { ReportRepository(get()) }
    single { ReportResultRepository(get()) }
    single<NamedEntityRepository<Account>>(Qualifiers.accountRepository) {
        accountRepositoryOf(get())
    }
    single<NamedEntityRepository<Category>>(Qualifiers.categoryRepository) {
        categoryRepositoryOf(get())
    }
    single<NamedEntityRepository<Supplier>>(Qualifiers.supplierRepository) {
        supplierRepositoryOf(get())
    }
    single<NamedEntityRepository<Program>>(Qualifiers.programRepository) {
        programRepositoryOf(get())
    }

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

    // Use cases
    single {
        GetItemOptionsUseCase(
            itemRepository = get()
        )
    }

    // ViewModel delegates
    single<CategoryOptionsProvider> {
        DefaultCategoryOptionsProvider(
            categoryRepository = get(Qualifiers.categoryRepository),
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
            programRepository = get(Qualifiers.programRepository),
            scope = CoroutineScope(Dispatchers.Default)
        )
    }
    single<AccountOptionsProvider> {
        DefaultAccountOptionsProvider(
            accountRepository = get(Qualifiers.accountRepository),
            scope = CoroutineScope(Dispatchers.Default)
        )
    }
    single<SupplierOptionsProvider> {
        DefaultSupplierOptionsProvider(
            supplierRepository = get(Qualifiers.supplierRepository),
            scope = CoroutineScope(Dispatchers.Default)
        )
    }

    // ViewModels
    viewModel(named("categoryViewModel")) {
        NamedEntityPageViewModel<Category>(get(Qualifiers.categoryRepository))
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
        NamedEntityPageViewModel<Program>(get(Qualifiers.programRepository))
    }
    viewModel(named("purchasingAccountViewModel")) {
        NamedEntityPageViewModel<Account>(get(Qualifiers.accountRepository))
    }
    viewModel {
        ReportDetailViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    viewModel {
        ReportHeadlineListViewModel(get(), get())
    }
    viewModel(named("supplierViewModel")) {
        NamedEntityPageViewModel<Supplier>(get(Qualifiers.supplierRepository))
    }

}
