package org.schoolustc.structureDsl

open class AreaProg (
    val x:IntProgression,
    val y:IntProgression,
    val z:IntProgression
){
    inline fun iterate(block:(Point) -> Unit){
        for(i in x){for (j in y){for (k in z){
            block(Point(i,j,k))
        }}}
    }
    val IntProgression.range get() = IntRange(first,last)
    val boundingArea get() = Area(
        x.range,
        y.range,
        z.range
    )
}