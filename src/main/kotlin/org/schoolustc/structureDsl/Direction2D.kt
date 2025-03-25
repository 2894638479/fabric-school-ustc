package org.schoolustc.structureDsl

import net.minecraft.core.Direction
import org.schoolustc.structureDsl.struct.StructGenConfig

enum class Direction2D {
    X1,X2,Z1,Z2;
    val isX get() = this == X1 || this == X2
    val isZ get() = this == Z1 || this == Z2
    val is1 get() = this == X1 || this == Z1
    val is2 get() = this == X2 || this == Z2
    val rotate get() = when(this){
        X1 -> Z1
        X2 -> Z2
        Z1 -> X1
        Z2 -> X2
    }
    val reverse get() = when(this){
        X1 -> X2
        X2 -> X1
        Z1 -> Z2
        Z2 -> Z1
    }
    val left get() = when(this){
        X1 -> Z2
        X2 -> Z1
        Z1 -> X1
        Z2 -> X2
    }
    val right get() = when(this){
        X1 -> Z1
        X2 -> Z2
        Z1 -> X2
        Z2 -> X1
    }
    inline val Area2D.width get() = width(this@Direction2D)
    inline val Area2D.length get() = length(this@Direction2D)
    inline val Area2D.w get() = w(this@Direction2D)
    inline val Area2D.l get() = l(this@Direction2D)
    infix fun parallel(other:Direction2D) = isX == other.isX
    infix fun vertical(other:Direction2D) = isX == other.isZ
    fun applyConfig(config:StructGenConfig): Direction2D {
        var d = this
        if(config.revX && d.isX) d = d.reverse
        if(config.revZ && d.isZ) d = d.reverse
        if(config.rotate) d = d.rotate
        return d
    }
    fun toMcDirection() = when(this){
        X1 -> Direction.WEST
        X2 -> Direction.EAST
        Z1 -> Direction.NORTH
        Z2 -> Direction.SOUTH
    }
}