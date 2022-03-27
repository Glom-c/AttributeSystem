package com.skillw.attsystem.util

import java.text.NumberFormat

object Format {
    @JvmStatic
    val format: NumberFormat by lazy {
        val numberFormat = NumberFormat.getInstance()
        numberFormat.isGroupingUsed = false
        numberFormat
    }

    @JvmStatic
    fun format(number: Number): String {
        return format.format(number)
    }

    fun Number.real(): String {
        return format.format(this)
    }
}