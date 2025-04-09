package org.schoolustc.structureDsl.struct

import org.schoolustc.structureDsl.Area
import org.schoolustc.structureDsl.Point

abstract class MyStructFixedSizeInfo<T:MyStructFixedSize>(id:String, val size: Point):MyStructInfo<T>(id){
    inline val xSize get() = size.x
    inline val ySize get() = size.y
    inline val zSize get() = size.z
    val fixedArea get() = Area(
        0..<size.x,
        0..<size.y,
        0..<size.z,
    )
}