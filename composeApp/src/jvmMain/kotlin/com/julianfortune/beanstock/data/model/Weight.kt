package com.julianfortune.beanstock.data.model

import com.julianfortune.beanstock.core.util.dividedBy
import java.math.BigInteger
import java.math.RoundingMode

// TODO(P3): Revisit accuracy at lower and upper magnitude bounds
// TODO(P5): Assess performance (and improve if needed)

data class Weight(val centigrams: Long) {

    companion object Companion {
        const val CENTIGRAMS_PER_KG = 100
        const val GRAMS_PER_OZ = 28.349523125
        const val OZ_PER_POUND = 16

        fun ofCentigrams(centigrams: Long): Weight {
            assert(centigrams >= 0) { "`Weight`s must be positive" }

            return Weight(centigrams)
        }

        fun ofCentigrams(centigrams: BigInteger): Weight {
            // NOTE: Sqlite supports at largest 8-byte signed integers
            assert(centigrams <= Long.MAX_VALUE.toBigInteger()) { "`Weight`s must fit in 8 bytes" }

            return ofCentigrams(centigrams.toLong())
        }

        fun ofKilograms(kg: Long): Weight {
            assert(kg >= 0) { "`Weight`s must be positive" }

            return Weight(kg * CENTIGRAMS_PER_KG)
        }

        fun ofImperial(pounds: Int, ounces: Float): Weight {
            assert(pounds >= 0) { "`pounds`s must be positive" }
            assert(ounces >= 0) { "`ounces`s must be positive" }
            assert(ounces < OZ_PER_POUND) { "`ounces` must be less than $OZ_PER_POUND" }

            val poundsInOzHundredths = pounds.toBigInteger() * (OZ_PER_POUND * 100).toBigInteger()
            val totalOzHundredths = poundsInOzHundredths.toBigDecimal() + (ounces * 100).toBigDecimal()
            val centigrams = (totalOzHundredths.times(GRAMS_PER_OZ.toBigDecimal()))

            return ofCentigrams(centigrams.setScale(0, RoundingMode.HALF_EVEN).toBigInteger())
        }
    }

    fun times(multiple: Long): Weight {
        return ofCentigrams(centigrams * multiple)
    }

    fun times(multiple: Number): Weight {
        return times(multiple.toLong())
    }

    fun toImperial(): Pair<Long, Float> {
        val totalOzHundredths = (centigrams.toBigDecimal() / GRAMS_PER_OZ.toBigDecimal()).toBigInteger()
        val (pounds, ozHundredths) = totalOzHundredths.dividedBy((OZ_PER_POUND * 100).toBigInteger())

        return Pair(pounds.toLong(), (ozHundredths.toFloat() / 100))
    }

    // TODO(P3): Refactor this to have a different return type that won't compromise on precision
    fun toPounds(): Float {
        val totalOzHundredths = centigrams.toBigDecimal() / GRAMS_PER_OZ.toBigDecimal()
        val poundsHundredths = totalOzHundredths
            .divide(OZ_PER_POUND.toBigDecimal())
            .setScale(0, RoundingMode.HALF_EVEN)
            .toFloat()

        return poundsHundredths / 100
    }
}
