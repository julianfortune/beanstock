package com.julianfortune.beanstock.data.codec

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import java.time.LocalDate
import java.time.format.DateTimeFormatter


object LocalDateCodec : Codec<LocalDate> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun deserialize(value: String): Result<LocalDate, CodecError> {
        return try {
            Success(LocalDate.parse(value, formatter))
        } catch (_: Throwable) {
            Failure(CodecError.InvalidInput(value))
        }
    }

    override fun serialize(value: LocalDate): String {
        return value.format(formatter)
    }
}
