package org.schoolustc.structs.blockBuilder

import org.schoolustc.structs.builder.LeafWallBuilder
import org.schoolustc.structs.listBuilser.LeafWallListBuilder
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.structure.StructureBuildScope
import org.schoolustc.structureDsl.structure.builder.MyStructListBuilder

class NormalBlock(
    val area:Area2D
):MyStructListBuilder<MyStruct> {
    override fun StructureBuildScope.build() = mutableListOf<MyStruct>().also { list ->
        Direction2D.entries.forEach {
            val wallArea = area.sliceStart(it,1).sliceStart(it.left,area.length(it.left) - 1)
            list += LeafWallListBuilder(wallArea,it.left).run { this@build.build() }
        }
    }
}