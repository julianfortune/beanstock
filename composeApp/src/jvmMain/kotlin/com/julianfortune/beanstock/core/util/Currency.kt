package com.julianfortune.beanstock.core.util

// Without any '$'
fun formatCents(cents: Long, abbreviated: Boolean = false) : String {
    val dollars = cents / 100
    val remainingCents = cents % 100
    return if (abbreviated && remainingCents == 0L) {
        "$dollars"
    } else {
        String.format("%d.%02d", dollars, remainingCents)
    }
}
