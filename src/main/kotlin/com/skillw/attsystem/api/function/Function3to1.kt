package com.skillw.attsystem.api.function

fun interface Function3to1<A, B, C, R> {
    fun invoke(a: A, b: B, c: C): R
}