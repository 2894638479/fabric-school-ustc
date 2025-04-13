package org.schoolustc.structureDsl

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class Pt(
    val x:Double,
    val z:Double
){
    fun distanceTo(other: Pt) = sqrt((x - other.x).pow(2) + (z - other.z).pow(2))
    fun offset(orientation: Orientation2D, length:Double): Pt {
        val rad = orientation.rad
        return Pt(
            x + cos(rad) * length,
            z - sin(rad) * length
        )
    }
    fun atOrientationOf(orientation: Orientation2D, other: Pt):Boolean{
        val rad = orientation.rad
        val rx = cos(rad)
        val rz = -sin(rad)
        return (rx*(x - other.x) + rz*(z - other.z)) > 0
    }
    fun inArea2D(area:Area2D) = area.x1 < x
            && area.x2 > x
            && area.z1 < z
            && area.z2 > z
}