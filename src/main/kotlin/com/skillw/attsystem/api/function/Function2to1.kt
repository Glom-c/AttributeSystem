package com.skillw.attsystem.api.function

fun interface Function2to1<A, B, R> {
    fun invoke(a: A, b: B): R
}