package org.schoolustc.calc

import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Orientation2D
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.match
import kotlin.math.*

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
        return (rx*(x - other.x) + rz*(z - other.z)) > -0.1
    }
    fun orientationTo(other:Pt):Orientation2D{
        val rad = acos((other.x - this.x) / distanceTo(other).match { it != 0.0 }).let { if(other.z > z) - it else it }
        return Orientation2D.byRad(rad)
    }
    fun inArea2D(area: Area2D) = area.x1 < x
            && area.x2 > x
            && area.z1 < z
            && area.z2 > z
    fun toPoint(y:Int) = Point(x.roundToInt(),y,z.roundToInt())
    inline fun toPoint(y:(Int,Int)->Int):Point{
        val x = x.roundToInt()
        val z = z.roundToInt()
        return Point(x,y(x,z),z)
    }
}