package org.schoolustc.structs.builder

import org.schoolustc.structs.Road
import org.schoolustc.structs.Splitter
import org.schoolustc.structs.Street
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.match
import org.schoolustc.structureDsl.struct.IsRoad
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structureDsl.struct.builder.MyStructBuilder

class RoadBuilder <T:MyStruct,V>  (
    val area:Area2D,
    val direction: Direction2D,
    val type: V,
): MyStructBuilder<T> where V:MyStructInfo<T>,V:IsRoad {
    override fun build() = direction.run {
        area.width.match(type.width)
        val config = StructGenConfig.byDirection(area,0,direction,type)
        when(type){
            Street -> Street(config, area.length) as T
            Road -> Road(config,area.length) as T
            Splitter -> Splitter(config,area.length) as T
            else -> error("unknown type: $type")
        }
    }
}