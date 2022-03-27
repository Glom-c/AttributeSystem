package com.skillw.attsystem.api.operation

fun interface Operation {
    fun run(a: Double, b: Double): Double
}