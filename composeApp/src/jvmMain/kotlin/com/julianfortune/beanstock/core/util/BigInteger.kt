package com.julianfortune.beanstock.core.util

import java.math.BigInteger

data class DivisionResult(val quotient: BigInteger, val remainder: BigInteger)

fun BigInteger.dividedBy(second: BigInteger): DivisionResult {
    val results = this.divideAndRemainder(second)
    return DivisionResult(results[0], results[1])
}
