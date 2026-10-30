package com.marioespinoza.mibalance.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object Money {
    fun parseMinor(input: String): Long? {
        val value = input.trim()
        if (!Regex("[0-9]{1,10}([.,][0-9]{1,2})?").matches(value)) return null
        return runCatching { BigDecimal(value.replace(',', '.')).movePointRight(2).longValueExact() }
            .getOrNull()?.takeIf { it in 1..999_999_999_999L }
    }
    fun input(minor: Long): String = BigDecimal.valueOf(minor, 2).toPlainString()
    fun format(minor: Long, locale: Locale = Locale.getDefault()): String = NumberFormat.getCurrencyInstance(locale).apply {
        currency = Currency.getInstance("CRC")
        if (this is DecimalFormat) decimalFormatSymbols = decimalFormatSymbols.apply { currencySymbol = "₡" }
        minimumFractionDigits = 2; maximumFractionDigits = 2; roundingMode = RoundingMode.UNNECESSARY
    }.format(BigDecimal.valueOf(minor, 2))
}
