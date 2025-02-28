package org.schoolustc.structureDsl

import net.minecraft.world.level.levelgen.structure.BoundingBox
import kotlin.math.max
import kotlin.math.min


class Area(
    val x:IntRange,
    val y:IntRange,
    val z:IntRange
) {
    inline fun iterate(block:(Point) -> Unit){
        for(i in x){for (j in y){for (k in z){
            block(Point(i,j,k))
        }}}
    }
    fun getP1() = Point(x.first,y.first,z.first)
    fun getP2() = Point(x.last,y.last,z.last)
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
}