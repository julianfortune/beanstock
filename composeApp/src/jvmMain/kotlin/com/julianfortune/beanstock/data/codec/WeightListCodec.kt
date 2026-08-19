package com.julianfortune.beanstock.data.codec

import com.julianfortune.beanstock.data.model.Weight
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success

// TODO: Tests
object WeightListCodec :
    com.julianfortune.beanstock.data.codec.Codec<List<com.julianfortune.beanstock.data.model.Weight>> {
    const val SEPARATOR = ", "

    override fun deserialize(value: String): Result<List<com.julianfortune.beanstock.data.model.Weight>, com.julianfortune.beanstock.data.codec.CodecError> {
        try {
            require(value.startsWith("["))
            require(value.endsWith("]"))

            val elementString = value
                // Remove leading `[`
                .drop(1)
                // Remove trailing `]`
                .dropLast(1)

            if (elementString == "") return Success(emptyList())

            return Success(elementString.split(SEPARATOR).map {
                Weight.ofCentigrams(it.toLong())
            })
        } catch (_: Throwable) {
            return Failure(CodecError.InvalidInput(value))
        }
    }

    override fun serialize(value: List<Weight>): String {
        val elements = value.joinToString(SEPARATOR) { it.centigrams.toString() }
        return "[$elements]"
    }
}