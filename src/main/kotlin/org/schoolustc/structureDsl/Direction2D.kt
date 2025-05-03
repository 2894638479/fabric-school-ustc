package org.schoolustc.structureDsl

import net.minecraft.core.Direction
import net.minecraft.core.Direction.*
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BlockStateProperties.*
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

enum class Direction2D {
    XPlus,XMin,ZPlus,ZMin;
    val isX get() = this == XPlus || this == XMin
    val isZ get() = this == ZPlus || this == ZMin
    val isPlus get() = this == XPlus || this == ZPlus
    val isMin get() = this == XMin || this == ZMin
    val rotate get() = when(this){
        XPlus -> ZPlus
        XMin -> ZMin
        ZPlus -> XPlus
        ZMin -> XMin
    }
    val reverse get() = when(this){
        XPlus -> XMin
        XMin -> XPlus
        ZPlus -> ZMin
        ZMin -> ZPlus
    }
    val left get() = when(this){
        XPlus -> ZMin
        XMin -> ZPlus
        ZPlus -> XPlus
        ZMin -> XMin
    }
    val right get() = when(this){
        XPlus -> ZPlus
        XMin -> ZMin
        ZPlus -> XMin
        ZMin -> XPlus
    }
    val plus get() = if(isX) XPlus else ZPlus
    val min get() = if(isX) XMin else ZMin
    inline val Area2D.width get() = width(this@Direction2D)
    inline val Area2D.length get() = length(this@Direction2D)
    inline val Area2D.w get() = w(this@Direction2D)
    inline val Area2D.l get() = l(this@Direction2D)
    infix fun parallel(other:Direction2D) = isX == other.isX
    infix fun vertical(other:Direction2D) = isX == other.isZ
    fun applyConfig(config: StructGenConfig): Direction2D {
        var d = this
        if(config.revX && d.isX) d = d.reverse
        if(config.revZ && d.isZ) d = d.reverse
        if(config.rotate) d = d.rotate
        return d
    }
    fun toMcDirection() = when(this){
        XPlus -> Direction.EAST
        XMin -> Direction.WEST
        ZPlus -> Direction.SOUTH
        ZMin -> Direction.NORTH
    }
    fun toMcProperty() = when(this){
        XPlus -> BlockStateProperties.EAST
        XMin -> BlockStateProperties.WEST
        ZPlus -> BlockStateProperties.SOUTH
        ZMin -> BlockStateProperties.NORTH
    }
    fun toMcWallProperty() = when(this){
        XPlus -> EAST_WALL
        XMin -> WEST_WALL
        ZPlus -> SOUTH_WALL
        ZMin -> NORTH_WALL
    }
    fun toInt() = when(this){
        XPlus -> 0
        XMin -> 1
        ZPlus -> 2
        ZMin -> 3
    }
    fun toDirection() = when(this){
        XPlus -> org.schoolustc.structureDsl.Direction.XPlus
        XMin -> org.schoolustc.structureDsl.Direction.XMin
        ZPlus -> org.schoolustc.structureDsl.Direction.ZPlus
        ZMin -> org.schoolustc.structureDsl.Direction.ZMin
    }
    fun toOrientation() = Orientation2D(
        when(this){
            XPlus -> 0.0
            XMin -> 180.0
            ZPlus -> 270.0
            ZMin -> 90.0
        }
    )
    companion object {
        fun fromInt(int: Int) = when(int){
            0 -> XPlus
            1 -> XMin
            2 -> ZPlus
            3 -> ZMin
            else -> error("unknown Direction2D int: $int")
        }
        fun fromBool(isX:Boolean,isPlus:Boolean):Direction2D{
            var result = 0
            if(!isX) result += 2
            if(!isPlus) result += 1
            return fromInt(result)
        }
        fun fromMcDirection(direction: Direction) = when(direction){
            Direction.EAST -> XPlus
            Direction.WEST -> XMin
            Direction.SOUTH -> ZPlus
            Direction.NORTH -> ZMin
            else -> null
        }
    }
}