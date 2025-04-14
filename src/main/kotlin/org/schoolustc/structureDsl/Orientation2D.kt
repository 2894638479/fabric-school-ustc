package org.schoolustc.structureDsl

import kotlin.math.PI

@JvmInline
value class Orientation2D(val value:Double) {
    fun left(value: Double) = Orientation2D(this.value + value)
    fun right(value: Double) = Orientation2D(this.value - value)
    val reverse get() = Orientation2D(value + 180)
    val rad get() = value * PI / 180
    companion object {
         fun byRad(rad:Double) = Orientation2D(rad / PI * 180)
    }
    fun nearestDirection():Direction2D{
        var final = value % 360
        if(final < 0) final += 360
        return if(final < 45) Direction2D.XPlus
        else if (final < 135) Direction2D.ZMin
        else if (final < 225) Direction2D.XMin
        else if(final < 315) Direction2D.ZPlus
        else Direction2D.XPlus
    }
}