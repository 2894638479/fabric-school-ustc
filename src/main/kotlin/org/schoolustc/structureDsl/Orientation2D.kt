package org.schoolustc.structureDsl

import kotlin.math.PI

@JvmInline
value class Orientation2D(val value:Double) {
    fun left(value: Double) = Orientation2D(this.value + value)
    fun right(value: Double) = Orientation2D(this.value - value)
    val reverse get() = Orientation2D(value + 180)
    val rad get() = value * PI / 180
}