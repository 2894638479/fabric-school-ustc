package org.schoolustc.structureDsl.struct

import org.schoolustc.structureDsl.Area
import org.schoolustc.structureDsl.Point

abstract class MyStructFixedSizeInfo<T:MyStructFixedSize>(id:String, val size: Point):MyStructInfo<T>(id){
    inline val xSize get() = size.x
    inline val ySize get() = size.y
    inline val zSize get() = size.z
    inline val xMax get() = size.x - 1
    inline val yMax get() = size.y - 1
    inline val zMax get() = size.z - 1
    val fixedArea get() = Area(
        0..<size.x,
        0..<size.y,
        0..<size.z,
    )
    inline val xRange get() = fixedArea.x
    inline val yRange get() = fixedArea.y
    inline val zRange get() = fixedArea.z
}