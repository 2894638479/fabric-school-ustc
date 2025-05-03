package org.schoolustc.structureDsl.struct

import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

abstract class MyStructFixedWidth(
    info: MyStructFixedWidthInfo<*>,
    config: StructGenConfig,
    val length:Int
):MyStructWithConfig(info, run {
    if(info.defaultDirection.isX) Point(length,10,info.width)
    else Point(info.width,10,length)
},config)