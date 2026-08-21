package com.julianfortune.beanstock.ui.common.input

import com.julianfortune.beanstock.ui.common.data.Dynamic
import com.julianfortune.beanstock.ui.common.data.Option
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds


@OptIn(ExperimentalCoroutinesApi::class)
class AsyncDynamicOptionsStateHolderJUnitTest {

    private val testOptions = listOf(Option(id = 1L, label = "Option 1"))

    @Test
    fun `emits expected state progression over time`() = runTest {
        // GIVEN
        val throttlePeriod = 100.milliseconds
        val stateHolder = AsyncDynamicOptionsStateHolder(
            getOptions = { flowOf(testOptions) },
            throttlePeriod
        )

        val emissions = mutableListOf<Dynamic<List<Option<Long>>>>()
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            stateHolder.options.collect {
                emissions.add(it)
            }
        }

        assertThat(emissions.size).isEqualTo(1)
        assertThat(emissions.last()).isEqualTo(Dynamic.Present(testOptions))

        stateHolder.onQueryChanged("t")
        assertThat(emissions.size).isEqualTo(2)
        assertThat(emissions.last()).isEqualTo(Dynamic.Loading)

        advanceTimeBy(throttlePeriod * 2)
        assertThat(emissions.size).isEqualTo(3)
        assertThat(emissions.last()).isEqualTo(Dynamic.Present(testOptions))

        stateHolder.onQueryChanged("te")
        assertThat(emissions.size).isEqualTo(4)
        assertThat(emissions.last()).isEqualTo(Dynamic.Loading)

        advanceTimeBy(throttlePeriod * 2)
        assertThat(emissions.size).isEqualTo(5)
        assertThat(emissions.last()).isEqualTo(Dynamic.Present(testOptions))

        collectJob.cancel()
    }
}
