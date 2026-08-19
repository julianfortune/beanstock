package com.julianfortune.beanstock.ui.common

import com.julianfortune.beanstock.data.model.Weight
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

fun formatLocalDate(d: LocalDate, style: FormatStyle = FormatStyle.MEDIUM): String {
    val usDateFormatter = DateTimeFormatter.ofLocalizedDate(style).withLocale(Locale.US)
    return d.format(usDateFormatter)
}

fun formatWeight(weight: Weight): String {
    val (lbs, oz) = weight.toImperial()
    val lbsString = if (lbs != 0L) "${lbs}lb" else null
    val ozString = if (oz > 0) {
        oz.toString().removeSuffix(".0") + "oz"
    } else null

    return listOfNotNull(lbsString, ozString).joinToString(" ")
}
