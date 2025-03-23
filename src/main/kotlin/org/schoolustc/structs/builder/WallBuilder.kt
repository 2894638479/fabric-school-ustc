package org.schoolustc.structs.builder

import org.schoolustc.structs.OuterWall
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.match
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structureDsl.struct.builder.MyStructBuilder

class WallBuilder(
    val direction:Direction2D,
    val area: Area2D
):MyStructBuilder<OuterWall> {
    val config = StructGenConfig.byDirection(area,0,direction,OuterWall)
    override fun build() = direction.run {
        area.width.match(OuterWall.width)
        OuterWall(config,area.length)
    }
}