package com.julianfortune.beanstock.core.codec

import com.julianfortune.beanstock.core.util.unwrapUnsafe
import com.julianfortune.beanstock.data.codec.LocalDateCodec
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDate

class LocalDateCodecTest {

    @ParameterizedTest
    @CsvSource(
        "2000-01-01, 10957",
        "2025-10-01, 20362",
        "2025-11-10, 20402",
    )
    fun deserialize(input: String, expectedEpochDay: Long) {
        // WHEN
        val result = LocalDateCodec.deserialize(input)

        // THEN
        assertThat(result).isInstanceOf(Success::class.java)
        assertThat(result.unwrapUnsafe().toEpochDay()).isEqualTo(expectedEpochDay)
    }

    @ParameterizedTest
    @CsvSource(
        "10957, 2000-01-01",
        "20362, 2025-10-01",
        "20402, 2025-11-10",
    )
    fun serializeWeightUnit(givenEpochDay: Long, expectedString: String) {
        // GIVEN
        val localDate = LocalDate.ofEpochDay(givenEpochDay)

        // WHEN
        val result = LocalDateCodec.serialize(localDate)

        // THEN
        assertThat(result).isEqualTo(expectedString)
    }

    @ParameterizedTest
    @CsvSource(
        "invalid",
        "2025-13-1",
        "2025-01-42",
    )
    fun deserializeInvalidInput(invalidInput: String) {
        // WHEN
        val result = LocalDateCodec.deserialize(invalidInput)

        // THEN
        assertThat(result).isInstanceOf(Failure::class.java)
    }
}
