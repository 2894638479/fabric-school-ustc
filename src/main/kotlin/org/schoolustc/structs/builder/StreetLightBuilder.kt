package org.schoolustc.structs.builder

import org.schoolustc.structs.StreetLight
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structureDsl.struct.builder.MyStructBuilder

class StreetLightBuilder(
    val pos:Point,
    val direction:Direction2D
):MyStructBuilder<StreetLight> {
    override fun build(): StreetLight {
        val area = Area2D(pos.x..pos.x,pos.z..pos.z).expand(direction,1)
        val config = StructGenConfig.byDirection(area, pos.y,direction,StreetLight)
        return StreetLight(config)
    }
}