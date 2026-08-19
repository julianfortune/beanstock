package com.julianfortune.beanstock.core.codec

import com.julianfortune.beanstock.data.codec.CostStatusCodec
import com.julianfortune.beanstock.data.model.CostStatus
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.valueOrNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CostStatusCodecTest {

    @ParameterizedTest
    @CsvSource(
        "PURCHASED, PURCHASED",
        "NO_COST, NO_COST",
    )
    fun deserialize(input: String, expected: CostStatus) {
        // WHEN
        val result = CostStatusCodec.deserialize(input)

        // THEN
        assertThat(result).isInstanceOf(Success::class.java)
        assertThat(result.valueOrNull()).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        "PURCHASED, PURCHASED",
        "NO_COST, NO_COST",
    )
    fun serialize(costStatus: CostStatus, expected: String) {
        // WHEN
        val result = CostStatusCodec.serialize(costStatus)

        // THEN
        assertThat(result).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        "invalid",
    )
    fun deserializeInvalidInput(invalidInput: String) {
        // WHEN
        val result = CostStatusCodec.deserialize(invalidInput)

        // THEN
        assertThat(result).isInstanceOf(Failure::class.java)
    }

}
