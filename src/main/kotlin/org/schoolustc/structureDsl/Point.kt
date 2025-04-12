package org.schoolustc.structureDsl

import com.google.common.math.IntMath.pow
import net.minecraft.core.BlockPos
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.schoolustc.structureDsl.struct.StructGenConfig
import kotlin.math.sqrt

class Point(
    val x:Int,
    val y:Int,
    val z:Int
) {
    val blockPos get() = BlockPos(x,y,z)
    inline fun finalPos(config: StructGenConfig,y:(Int,Int)->Int = { _,_-> this.y + config.pos.y }):Point{
        val xAdd = if (config.revX) - x else x
        val zAdd = if (config.revZ) - z else z
        val finalX = config.pos.x + if (config.rotate) zAdd else xAdd
        val finalZ = config.pos.z + if (config.rotate) xAdd else zAdd
        val finalY = y(finalX,finalZ)
        return Point(finalX, finalY, finalZ)
    }
    operator fun plus(other:Point) = Point(
        x + other.x,
        y + other.y,
        z + other.z
    )
    fun offset(direction:Direction,count:Int = 1):Point{
        var x = x
        var y = y
        var z = z
        when(direction){
            Direction.XPlus -> x += count
            Direction.XMin -> x -= count
            Direction.YPlus -> y += count
            Direction.YMin -> y -= count
            Direction.ZPlus -> z += count
            Direction.ZMin -> z -= count
        }
        return Point(x,y,z)
    }
    fun distanceTo(other: Point) = sqrt((pow(x-other.x,2) + pow(y-other.y,2) + pow(z-other.z,2)).toDouble())
    override fun equals(other: Any?): Boolean {
        return (other as? Point)?.let {
            it.x == x && it.y == y && it.z == z
        } ?: false
    }

    override fun toString(): String {
        return "Point{x:$x,y:$y,z:$z}"
    }
    fun inBox(box:BoundingBox) = box.isInside(x,y,z)
    fun inArea2D(area:Area2D) = x in area.x && z in area.z
    fun atDirectionOf(direction:Direction2D,other:Point) = when(direction){
        Direction2D.XPlus -> x > other.x
        Direction2D.XMin -> x < other.x
        Direction2D.ZPlus -> z > other.z
        Direction2D.ZMin -> z < other.z
    }
}
val BlockPos.point get() = Point(x,y,z)