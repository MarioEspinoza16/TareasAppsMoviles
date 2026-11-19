package com.marioespinoza.mibalance.util

import java.util.Locale

object Money {
    fun parseMinor(input: String): Long? {
        val parts = input.trim().replace(',', '.').split('.')
        if (parts.size > 2 || parts[0].length !in 1..10) return null
        val whole = parts[0].toLongOrNull() ?: return null
        if (parts[0].any { it !in '0'..'9' }) return null

        var cents = 0L
        if (parts.size == 2) {
            val fraction = parts[1]
            if (fraction.length !in 1..2 || fraction.any { it !in '0'..'9' }) return null
            cents = fraction.toLong()
            if (fraction.length == 1) cents *= 10
        }
        val amount = whole * 100 + cents
        return if (amount > 0) amount else null
    }

    fun input(minor: Long): String = formatNumber(minor, Locale.US)

    fun format(minor: Long, locale: Locale): String = "₡" + formatNumber(minor, locale)

    private fun formatNumber(minor: Long, locale: Locale): String {
        val sign = if (minor < 0) "-" else ""
        val whole = kotlin.math.abs(minor / 100)
        val cents = kotlin.math.abs(minor % 100)
        val separator = if (locale.language == "es") "," else "."
        val fraction = if (cents < 10) "0" + cents else cents.toString()
        return sign + whole + separator + fraction
    }
}
