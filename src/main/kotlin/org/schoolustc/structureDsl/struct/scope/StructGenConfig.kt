package org.schoolustc.structureDsl.struct.scope

import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.match
import org.schoolustc.structureDsl.struct.MyStructInfo

class StructGenConfig(
    val pos: Point,
    val revX: Boolean,
    val revZ: Boolean,
    val rotate: Boolean
){
    constructor(pos: Point):this(pos,false,false,false)
    fun toIntArray():IntArray{
        var blInt = 0
        if(revX) blInt += 1
        if(revZ) blInt += 2
        if(rotate) blInt += 4
        return intArrayOf(pos.x,pos.y,pos.z,blInt)
    }
    companion object{
        fun fromIntArray(arr:IntArray): StructGenConfig {
            arr.size.match(4)
            val point = Point(arr[0],arr[1],arr[2])
            val blInt = arr[3]
            return StructGenConfig(
                point,
                blInt and 1 != 0,
                blInt and 2 != 0,
                blInt and 4 != 0
            )
        }
        fun byDirection(area:Area2D,y:Int,direction:Direction2D,info: MyStructInfo<*>): StructGenConfig {
            val revX:Boolean
            val revZ:Boolean
            val rotate:Boolean
            val d = info.defaultDirection
            when(direction){
                d -> {
                    revX = false
                    revZ = false
                    rotate = false
                }
                d.reverse -> {
                    revX = true
                    revZ = true
                    rotate = false
                }
                d.left -> {
                    revX = true
                    revZ = false
                    rotate = true
                }
                d.right -> {
                    revX = false
                    revZ = true
                    rotate = true
                }
                else -> error("runtime error")
            }
            val pos = if(rotate) Point(
                if(revZ) area.x2 else area.x1,
                y,
                if(revX) area.z2 else area.z1
            ) else Point(
                if(revX) area.x2 else area.x1,
                y,
                if (revZ) area.z2 else area.z1
            )
            return StructGenConfig(pos,revX, revZ, rotate)
        }
    }
}