package org.schoolustc.structs.builder

import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.struct.MyStructFixedWidth
import org.schoolustc.structureDsl.struct.MyStructFixedWidthInfo

class RoadBuilder <T:MyStructFixedWidth,V:MyStructFixedWidth> (
    area: Area2D,
    direction: Direction2D,
    type: MyStructFixedWidthInfo<T>,
    val side:MyStructFixedWidthInfo<V>?
) : FixedWidthBuilder<T>(area,direction,type) {

}