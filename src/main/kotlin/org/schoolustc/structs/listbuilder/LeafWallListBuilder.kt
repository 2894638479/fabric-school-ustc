package org.schoolustc.structs.listbuilder

import org.schoolustc.structs.LeafWall
import org.schoolustc.structs.builder.LeafWallBuilder
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Area2D.Companion.area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.first
import org.schoolustc.structureDsl.struct.builder.MyStructListBuilder
import org.schoolustc.structureDsl.structure.StructureBuildScope
import org.schoolustc.structureDsl.structure.split

class LeafWallListBuilder(
    val area: Area2D,
    val direction:Direction2D,
    val maxLen:Int = 10
) : MyStructListBuilder<LeafWall>() {
    override fun StructureBuildScope.build() = mutableListOf<LeafWall>().apply {
        direction.run {
            area.l.first(area.length + 1).split(maxLen).zipWithNext { a, b ->
                add(LeafWallBuilder(direction,area2D(a..<b,area.w)).build())
            }
        }
    }
}