package com.julianfortune.beanstock.data.codec

import dev.forkhandles.result4k.Result

interface Codec<A> {
    fun deserialize(value: String): Result<A, com.julianfortune.beanstock.data.codec.CodecError>
    fun serialize(value: A): String
}
