package org.schoolustc.structureDsl.structure

import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.MyStructInfo

abstract class MyStructFixedSizeInfo<T:MyStructFixedSize>(id:String, val size: Point):MyStructInfo<T>(id){
    inline val xSize get() = size.x
    inline val ySize get() = size.y
    inline val zSize get() = size.z
}