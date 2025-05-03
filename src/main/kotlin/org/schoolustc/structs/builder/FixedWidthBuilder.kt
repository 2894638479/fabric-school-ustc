package org.schoolustc.structs.builder

import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.match
import org.schoolustc.structureDsl.struct.MyStructFixedWidth
import org.schoolustc.structureDsl.struct.MyStructFixedWidthInfo
import org.schoolustc.structureDsl.struct.scope.StructGenConfig
import org.schoolustc.structureDsl.struct.builder.MyStructBuilder

open class FixedWidthBuilder <T:MyStructFixedWidth>  (
    val area:Area2D,
    val direction: Direction2D,
    val type: MyStructFixedWidthInfo<T>
): MyStructBuilder<T> {
    override fun build() = direction.run {
        area.width.match(type.width)
        val config = StructGenConfig.byDirection(area,0,direction,type)
        type.constructor(config,area.length)
    }
}