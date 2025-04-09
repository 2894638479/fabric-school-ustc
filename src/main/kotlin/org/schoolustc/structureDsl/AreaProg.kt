package org.schoolustc.structureDsl

open class AreaProg (
    val x:IntProgression,
    val y:IntProgression,
    val z:IntProgression
):Sequence<Point>{
    val IntProgression.range get() = IntRange(first,last)
    val boundingArea get() = Area(
        x.range,
        y.range,
        z.range
    )

    private val seq = sequence {
        for(i in x){for (j in y){for (k in z){
            yield(Point(i,j,k))
        }}}
    }
    override fun iterator() = seq.iterator()
}