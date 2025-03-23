package org.schoolustc.structs.listBuilser

import org.schoolustc.structs.builder.RoadBuilder
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.match
import org.schoolustc.structureDsl.struct.MyRoadStruct
import org.schoolustc.structureDsl.struct.MyRoadStructInfo
import org.schoolustc.structureDsl.structure.StructureBuildScope
import org.schoolustc.structureDsl.structure.builder.MyStructListBuilder

class RoadListBuilder <T:MyRoadStruct>  (
    val area:Area2D,
    val direction: Direction2D,
    val type: MyRoadStructInfo<T>
): MyStructListBuilder<T> {
    override fun StructureBuildScope.build() = direction.run {
        area.width.match(type.width)
        mutableListOf<T>().apply {
            var left = area.length
            while(left > type.period){
                val current = area.length - left
                val a = area.slice(direction,current..<current + type.period)
                add(RoadBuilder(a,direction,type).build())
                left -= type.period
            }
            val a = area.slice(direction,area.length - left..<area.length)
            add(RoadBuilder(a,direction,type).build())
        }
    }
}