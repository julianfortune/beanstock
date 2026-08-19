package com.julianfortune.beanstock.ui.feature.savedweight.form

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SavedWeightStateHolderTest {

    private lateinit var stateHolder: SavedWeightStateHolder

    @BeforeEach
    fun setup() {
        stateHolder = SavedWeightStateHolder()
    }

    @Test
    fun `Initial state is correct`() {
        // GIVEN / WHEN
        val state = stateHolder.uiState

        // THEN
        assertThat(state.lbs.value).isEmpty()
        assertThat(state.oz.value).isEmpty()

        assertThat(stateHolder.validData).isNull()
        assertThat(state.isValid).isFalse()
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "5, 8.5, 250893",
            "1,    ,  45359",
            " ,  .1,    283",
        ]
    )
    fun `Valid inputs result in a submittable state`(pounds: String?, ounces: String?, expectedCentigrams: Long) {
        println("pounds: $pounds, ounces: $ounces")
        // GIVEN / WHEN
        stateHolder.onPoundsChange(pounds ?: "")
        stateHolder.onOuncesChange(ounces ?: "")

        // THEN
        assertThat(stateHolder.uiState.isValid).isTrue()
        assertThat(stateHolder.validData).isNotNull()
        assertThat(stateHolder.validData?.weight?.centigrams).isEqualTo(expectedCentigrams)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            " -1,    5",
            "   ,     ",
            "abc,     ",
            "   ,    .",
            "   , -1.0",
        ]
    )
    fun `Invalid inputs result in an invalid state`(pounds: String?, ounces: String?) {
        // GIVEN / WHEN
        stateHolder.onPoundsChange(pounds ?: "")
        stateHolder.onOuncesChange(ounces ?: "")

        // THEN
        assertThat(stateHolder.uiState.isValid).isFalse()
        assertThat(stateHolder.validData).isNull()
    }

    @Test
    fun `clear resets everything correctly`() {
        // GIVEN
        stateHolder.onPoundsChange("1")
        stateHolder.onOuncesChange("8")

        // WHEN
        stateHolder.clear()

        // THEN
        val state = stateHolder.uiState
        assertThat(state.lbs.value).isEmpty()
        assertThat(state.oz.value).isEmpty()
        assertThat(state.isValid).isFalse()
        assertThat(stateHolder.validData).isNull()
    }
}