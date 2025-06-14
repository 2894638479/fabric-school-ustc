package org.schoolustc.structureDsl

import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.schoolustc.structureDsl.struct.scope.StructGenConfig


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
    fun length(direction:Direction) = when(direction){
        Direction.XPlus,Direction.XMin -> x.length
        Direction.YPlus,Direction.YMin -> y.length
        Direction.ZPlus,Direction.ZMin -> z.length
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
    fun applyConfig(config: StructGenConfig) = toArea2D().applyConfig(config).toArea(y)
    fun toBoundingBox(extendY:Boolean = true) = BoundingBox(
        x1,if(extendY) Int.MIN_VALUE else y1,z1,
        x2,if(extendY) Int.MAX_VALUE else y2,z2
    )
}