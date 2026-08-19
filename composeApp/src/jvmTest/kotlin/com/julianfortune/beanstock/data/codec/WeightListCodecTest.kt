package com.julianfortune.beanstock.data.codec

import com.julianfortune.beanstock.core.util.unwrapUnsafe
import com.julianfortune.beanstock.data.model.Weight
import dev.forkhandles.result4k.Failure
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class WeightListCodecTest {

    @ParameterizedTest
    @CsvSource(
        value = [
            "[]        |     |",
            "[100]     | 100 |",
            "[0, 0]    |   0 | 0",
            "[100, 20] | 100 | 20",
        ],
        delimiter = '|'
    )
    fun deserializeSucceeds(input: String, firstExpected: Long?, secondExpected: Long?) {
        // GIVEN / WHEN
        val result = WeightListCodec.deserialize(input).unwrapUnsafe()

        // THEN
        val expected = listOfNotNull(firstExpected, secondExpected).map { Weight(it) }
        assertThat(result).containsExactlyElementsOf(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "[ ]",
            "[, ]",
            "-100, 20",
            "[-100, 20]",
        ],
        delimiter = '|'
    )
    fun deserializeFails(input: String) {
        // GIVEN / WHEN
        val result = WeightListCodec.deserialize(input)

        // THEN
        assertThat(result is Failure).isTrue
        assertThat((result as Failure).reason is CodecError.InvalidInput).isTrue
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "        |   |   | [] ",
            "      1 |   |   | [1] ",
            " 100000 |   |   | [100000] ",
            "      1 | 2 | 3 | [1, 2, 3] ",
        ],
        delimiter = '|'
    )
    fun serialize(first: Long?, second: Long?, third: Long?, expected: String) {
        // GIVEN
        val elements = listOfNotNull(first, second, third).map { Weight(it) }

        // WHEN
        val result = WeightListCodec.serialize(elements)

        // THEN
        assertThat(result).isEqualTo(expected)
    }

}
