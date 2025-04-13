package org.schoolustc.structs.builder

import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.match
import org.schoolustc.structureDsl.struct.MyRoadStruct
import org.schoolustc.structureDsl.struct.MyRoadStructInfo
import org.schoolustc.structureDsl.struct.scope.StructGenConfig
import org.schoolustc.structureDsl.struct.builder.MyStructBuilder

class RoadBuilder <T:MyRoadStruct>  (
    val area:Area2D,
    val direction: Direction2D,
    val type: MyRoadStructInfo<T>
): MyStructBuilder<T> {
    override fun build() = direction.run {
        area.width.match(type.width)
        val config = StructGenConfig.byDirection(area,0,direction,type)
        type.constructor(config,area.length)
    }
}