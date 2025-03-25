package org.schoolustc.structs.builder

import org.schoolustc.structs.LeafWall
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.match
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structureDsl.struct.builder.MyStructBuilder

class LeafWallBuilder(
    val direction: Direction2D,
    val area: Area2D
): MyStructBuilder<LeafWall> {
    val config = StructGenConfig.byDirection(area,0,direction, LeafWall)
    override fun build() = direction.run {
        area.width.match(1)
        LeafWall(config,area.length)
    }
}