package org.schoolustc.structs.blockBuilder

import org.schoolustc.structs.builder.BuildingBuilder
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.from
import org.schoolustc.structureDsl.nextBool
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.structure.StructureBuildScope
import org.schoolustc.structureDsl.structure.builder.MyStructListBuilder

class BuildingBlock(
    val area: Area2D,
    val nextToWalls:List<Direction2D>,
    val nextToSplitter:List<Direction2D>
):MyStructListBuilder<MyStruct>() {
    override fun StructureBuildScope.build() = mutableListOf<MyStruct>().also { list ->
        list += NormalBlock(area, nextToWalls, nextToSplitter).build(this)
        val pos = area.middle(::y)
        val area = Area2D(pos.x..pos.x,pos.z..pos.z).expand(5)
        val direction = rand from Direction2D.entries.filter { it !in nextToWalls }
        val height = rand from 1..6
        val flatTop = !(height >= 5 && rand.nextBool(0.3f))
        list += BuildingBuilder(area,pos.y,direction,height,flatTop).build()
    }
}