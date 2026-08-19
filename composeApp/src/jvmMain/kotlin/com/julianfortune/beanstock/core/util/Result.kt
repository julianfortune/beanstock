package com.julianfortune.beanstock.core.util

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success

fun <T, E> Result<T, E>.unwrapUnsafe(): T = when (this) {
    is Success<T> -> value
    is Failure<E> -> throw UnexpectedFailureException("Tried to unwrap a failure: $this")
}
