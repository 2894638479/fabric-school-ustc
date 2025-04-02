package org.schoolustc.structs.listbuilder

import org.schoolustc.structs.builder.WallBuilder
import org.schoolustc.structs.builder.WallCornerBuilder
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Area2D.Companion.area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.match
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.builder.MyStructListBuilder
import org.schoolustc.structureDsl.structure.StructureBuildScope
import org.schoolustc.structureDsl.split

class WallListBuilder(
    val direction:Direction2D,
    val area: Area2D,
    val maxStep: Int = 10
): MyStructListBuilder<MyStruct>() {
    override fun StructureBuildScope.build() = direction.run {
        area.checkNotEmpty().width.match(1)
        mutableListOf<MyStruct>().also { list ->
            val split = area.l.split(maxStep)
            split.zipWithNext { a, b ->
                val area = area2D(a+ 1..b- 1,area.w)
                list += WallBuilder(direction,area).build()
            }
            split.dropLast(1).drop(1).forEach {
                val a = area2D(it..it,area.w)
                list += WallCornerBuilder(a.x1,a.z1).build()
            }
        }
    }
}