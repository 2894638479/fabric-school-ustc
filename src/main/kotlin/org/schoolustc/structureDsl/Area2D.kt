package org.schoolustc.structureDsl

class Area2D(
    val x:IntRange,
    val z:IntRange
){
    val w get() = x.last - x.first + 1
    val h get() = z.last - z.first + 1
    fun padding(i:Int) = Area2D(
        x.first + 1 .. x.last - 1,
        z.first + 1 .. z.last - 1
    )
    val x1 get() = x.first
    val x2 get() = x.last
    val z1 get() = z.first
    val z2 get() = z.last
}