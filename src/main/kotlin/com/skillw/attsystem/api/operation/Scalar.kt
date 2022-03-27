package com.skillw.attsystem.api.operation

object Scalar : BaseOperation("scalar") {
    override fun run(a: Double, b: Double): Double {
        return a * b
    }
}