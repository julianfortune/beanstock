package com.julianfortune.beanstock.ui.feature.report.list

import com.julianfortune.beanstock.data.repository.ReportRepository
import com.julianfortune.beanstock.ui.coordinator.report.ReportViewCoordinator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ReportHeadlineListViewModelTest {

    private val mockReportRepository = mockk<ReportRepository>()
    private val mockReportViewCoordinator = mockk<ReportViewCoordinator>(relaxed = true)

    private lateinit var viewModel: ReportHeadlineListViewModel

    @BeforeEach
    fun setUp() {
        every { mockReportRepository.getAllAsHeadlines() } returns flowOf(emptyList())

        viewModel = ReportHeadlineListViewModel(
            mockReportRepository,
            mockReportViewCoordinator,
        )
    }

    @Test
    fun onSelect() {
        // WHEN
        viewModel.onSelect(1)

        // THEN
        verify(exactly = 1) { mockReportViewCoordinator.view(1) }
    }

}
