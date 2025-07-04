package org.schoolustc.structureDsl

import com.google.common.math.IntMath.pow
import net.minecraft.core.BlockPos
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.schoolustc.calc.Pt
import org.schoolustc.structureDsl.struct.scope.StructGenConfig
import kotlin.math.sqrt

class Point(
    val x:Int,
    val y:Int,
    val z:Int
) {
    val blockPos get() = BlockPos(x,y,z)
    fun finalXZ(config: StructGenConfig):Point{
        val xAdd = if (config.revX) - x else x
        val zAdd = if (config.revZ) - z else z
        val finalX = config.pos.x + if (config.rotate) zAdd else xAdd
        val finalZ = config.pos.z + if (config.rotate) xAdd else zAdd
        return Point(finalX, y, finalZ)
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
    fun offset(direction:Direction2D,count:Int = 1):Point{
        var x = x
        var z = z
        when(direction){
            Direction2D.XPlus -> x += count
            Direction2D.XMin -> x -= count
            Direction2D.ZPlus -> z += count
            Direction2D.ZMin -> z -= count
        }
        return Point(x,y,z)
    }
    fun offsetX(count:Int) = Point(x+count,y,z)
    fun offsetY(count:Int) = Point(x,y+count,z)
    fun offsetZ(count:Int) = Point(x,y,z+count)
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

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }
    fun toPt() = Pt(x.toDouble(),z.toDouble())
}