package org.schoolustc.structs.builder

import org.schoolustc.structs.OuterWallCorner
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structureDsl.struct.builder.MyStructBuilder

class WallCornerBuilder(
    val x:Int,val z:Int
):MyStructBuilder<OuterWallCorner> {
    override fun build() = OuterWallCorner(StructGenConfig(Point(x,0,z),false,false,false))
}