package org.schoolustc.structureDsl

import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.schoolustc.structureDsl.struct.StructGenConfig
import kotlin.math.max
import kotlin.math.min


class Area(
    val x:IntRange,
    val y:IntRange,
    val z:IntRange
): Sequence<Point>{
    inline val x1 get() = x.first
    inline val x2 get() = x.last
    inline val y1 get() = y.first
    inline val y2 get() = y.last
    inline val z1 get() = z.first
    inline val z2 get() = z.last
    fun getP1() = Point(x.first,y.first,z.first)
    fun getP2() = Point(x.last,y.last,z.last)
    fun length(direction:Direction) = when(direction){
        Direction.XPlus,Direction.XMin -> x.length
        Direction.YPlus,Direction.YMin -> y.length
        Direction.ZPlus,Direction.ZMin -> z.length
    }
    fun boundingBox(config: StructGenConfig): BoundingBox {
        val p1 = getP1().finalPos(config)
        val p2 = getP2().finalPos(config)
        return BoundingBox(
            min(p1.x,p2.x),
            min(p1.y,p2.y),
            min(p1.z,p2.z),
            max(p1.x,p2.x),
            max(p1.y,p2.y),
            max(p1.z,p2.z),
        )
    }
    fun toArea2D() = Area2D(x,z)
    fun isEmpty() = x.isEmpty() || y.isEmpty() || z.isEmpty()
    fun ifEmpty(block:()->Unit) = apply { if(isEmpty()) block() }
    override fun toString(): String {
        return "x:$x1..$x2  " +
                "y:$y1..$y2  " +
                "z:$z1..$z2"
    }
    fun checkNotEmpty() = ifEmpty { error("empty area: $this") }
    operator fun contains(point: Point) = point.x in x && point.y in y && point.z in z
    private val seq = sequence {
        for(i in x){for (j in y){for (k in z){
            yield(Point(i,j,k))
        }}}
    }
    override fun iterator() = seq.iterator()
}