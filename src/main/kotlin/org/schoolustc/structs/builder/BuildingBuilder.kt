package org.schoolustc.structs.builder

import org.schoolustc.structs.Building
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structureDsl.struct.builder.MyStructBuilder

class BuildingBuilder(
    val area:Area2D,
    val y:Int,
    val direction:Direction2D,
    val height:Int,
    val flatTop:Boolean
):MyStructBuilder<Building> {
    override fun build(): Building {
        val config = StructGenConfig.byDirection(area,y,direction,Building)
        return Building(config,height,flatTop)
    }
}