package com.julianfortune.beanstock.data.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class WeightTest {

    @ParameterizedTest(name = "{index}: {0}lbs{1}oz")
    @CsvSource(
        value = [
            "0, 0.01", // Lower bound
            "0, 1",
            "2, 8",
            "624, 0",
            "5000, 1",
            "5000, 8",
            "100000, 1",
            "2094967295, 15", // Upper bound
        ]
    )
    fun ofImperialRoundTripProducesSame(pounds: Int, ounces: Float) {
        // WHEN
        val weight = Weight.ofImperial(pounds, ounces)
        val result = weight.toImperial()

        // THEN
        assertThat(result.first.toInt()).isEqualTo(pounds)
        assertThat(result.second).isEqualTo(ounces)
    }

}
