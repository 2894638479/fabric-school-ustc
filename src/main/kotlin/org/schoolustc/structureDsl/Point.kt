package org.schoolustc.structureDsl

import net.minecraft.core.BlockPos
import org.schoolustc.structureDsl.struct.StructGenConfig

data class Point(
    val x:Int,
    val y:Int,
    val z:Int
){
    val blockPos get() = BlockPos(x,y,z)
    fun finalPos(config: StructGenConfig):Point{
        val xAdd = if (config.revX) - x else x
        val zAdd = if (config.revZ) - z else z
        return Point(
            config.pos.x + if (config.rotate) zAdd else xAdd,
            config.pos.y + y,
            config.pos.z + if (config.rotate) xAdd else zAdd
        )
    }
    //对finalPos调用
    fun toSurface(getY:(Int, Int)->Int):Point{
        val finalPos = this
        return Point(
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
}
val BlockPos.point get() = Point(x,y,z)