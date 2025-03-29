package org.schoolustc.structs.blockBuilder

import org.schoolustc.structs.builder.LeafWallBuilder
import org.schoolustc.structs.builder.StreetLightBuilder
import org.schoolustc.structs.listBuilser.LeafWallListBuilder
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.nextBool
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.structure.StructureBuildScope
import org.schoolustc.structureDsl.structure.builder.MyStructListBuilder

class NormalBlock(
    val area:Area2D,
    val toOuterWall:(Direction2D)->Boolean
): MyStructListBuilder<MyStruct>() {
    override fun StructureBuildScope.build() = mutableListOf<MyStruct>().also { list ->
        val light = mutableListOf<StreetLightBuilder>()
        Direction2D.entries.forEach {
            val toOuterWall = toOuterWall(it)
            val wallArea = area.sliceStart(it,1).sliceStart(it.left,area.length(it.left) - 1)
            list += LeafWallListBuilder(wallArea,it.left).build(this)
            val lightArea = area.slice(it,1..1).slice(it.left,1..area.length(it.left) - 2)
            lightArea.iterate { x, z ->
                if (!toOuterWall && rand.nextBool(0.5f)) {
                    val li = StreetLightBuilder(x, z, it.reverse)
                    if(light.firstOrNull { it.distanceTo(li) < 7.0 } == null) {
                        light += li
                    }
                }
            }
        }
        list += light.map { it.build() }
    }
}