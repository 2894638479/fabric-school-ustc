package org.schoolustc.structs.builder

import org.schoolustc.structs.Sakura
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structureDsl.struct.builder.MyStructBuilder

class SakuraBuilder(
    val pos: Point,
):MyStructBuilder<Sakura> {
    override fun build(): Sakura {
        return Sakura(StructGenConfig(Point(pos.x - 5,pos.y,pos.z - 5),false,false,false))
    }
}