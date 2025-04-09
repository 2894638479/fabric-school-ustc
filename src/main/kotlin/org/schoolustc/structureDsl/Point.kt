package org.schoolustc.structureDsl

import com.google.common.math.IntMath.pow
import net.minecraft.core.BlockPos
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.schoolustc.structureDsl.struct.StructGenConfig
import kotlin.math.sqrt

open class Point(
    val x:Int,
    val y:Int,
    val z:Int
):Sequence<Point>{
    class FinalPoint(x:Int,y: Int,z: Int):Point(x,y,z)
    val blockPos get() = BlockPos(x,y,z)
    fun finalPos(config: StructGenConfig):FinalPoint{
        val xAdd = if (config.revX) - x else x
        val zAdd = if (config.revZ) - z else z
        return FinalPoint(
            config.pos.x + if (config.rotate) zAdd else xAdd,
            config.pos.y + y,
            config.pos.z + if (config.rotate) xAdd else zAdd
        )
    }
    fun finalSurfacePos(config:StructGenConfig,getY:(Int, Int)->Int):FinalPoint{
        val finalPos = finalPos(config)
        return FinalPoint(
            finalPos.x,
            y + getY(finalPos.x,finalPos.z),
            finalPos.z
        )
    }
    operator fun plus(other:Point) = Point(
        x + other.x,
        y + other.y,
        z + other.z
    )

    override fun iterator() = object : Iterator<Point> {
        private var point:Point? = this@Point
        override fun hasNext(): Boolean = point != null
        override fun next() = point?.apply { point = null } ?: throw NoSuchElementException()
    }
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