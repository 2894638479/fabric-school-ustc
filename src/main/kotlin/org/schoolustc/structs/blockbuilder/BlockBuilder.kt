package org.schoolustc.structs.blockbuilder

import org.schoolustc.structs.LeafWall
import org.schoolustc.structs.StreetLight
import org.schoolustc.structs.builder.LeafWallBuilder
import org.schoolustc.structs.builder.StreetLightBuilder
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.Area2D.Companion.area2D
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.builder.MyStructListBuilder
import org.schoolustc.structureDsl.structure.StructureBuildScope

abstract class BlockBuilder(val para: BlockBuilderPara): MyStructListBuilder<MyStruct>() {
    val area get() = para.area
    val nextToWalls get() = para.nextToWalls
    val nextToSplitter get() = para.nextToSplitter
    val openArea = Direction2D.entries.filter { it !in nextToWalls }.associateWith {
        val pos = para.scope.rand from area.l(it.left).padding(area.length(it.left) / 4)
        it.run { area2D(area.bound(this).range,pos-1..pos+1) }
    }
    open fun getLeafWalls():List<LeafWall> = mutableListOf<LeafWall>().also{ list ->
        Direction2D.entries.forEach {
            openArea[it]?.let { area1 ->
                it.run {
                    val wallArea1 = area2D(area.bound(it).range,area.w.first..<area1.w.first)
                    val wallArea2 = area2D(area.bound(it).range,area1.w.last+1..area.w.last)
                    list += LeafWallBuilder(it.left,wallArea1).build()
                    list += LeafWallBuilder(it.left,wallArea2).build()
                }
            } ?: run {
                val wallArea = area.sliceEnd(it, 1).sliceStart(it.left, area.length(it.left) - 1)
                list += LeafWallBuilder(it.left, wallArea).build()
            }
        }
    }
    open fun StructureBuildScope.getLights():List<StreetLight> {
        val light = mutableListOf<StreetLightBuilder>()
        val filterAreas = openArea.map { (direction,area) -> area.offset(direction,-1) }
        Direction2D.entries.forEach {
            val lightArea = area.slice(it.reverse, 1..1).slice(it.left, 1..area.length(it.left) - 2)
            lightArea.iterate { x, z ->
                val distance = if (it in nextToSplitter) 15.0 else 7.0
                if (it !in nextToWalls && rand.nextBool(0.5f)) {
                    val li = StreetLightBuilder(Point(x, y(x, z) + 1, z), it)
                    if (light.firstOrNull { it.pos.distanceTo(li.pos) < distance } == null
                        && filterAreas.firstOrNull { li.pos.inArea2D(it) } == null) {
                        light += li
                    }
                }
            }
        }
        return light.map { it.build() }
    }
}