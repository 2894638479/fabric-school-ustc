package org.schoolustc.structs.blockbuilder

import org.schoolustc.structs.LeafWall
import org.schoolustc.structs.StreetLight
import org.schoolustc.structs.blockbuilder.OpenRangeBuilder.Companion.toOpenArea
import org.schoolustc.structs.builder.LeafWallBuilder
import org.schoolustc.structs.builder.StreetLightBuilder
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.Area2D.Companion.area2D
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.builder.MyStructListBuilder
import org.schoolustc.structureDsl.structure.StructureBuildScope
import kotlin.math.absoluteValue

abstract class BlockBuilder(val para: BlockBuilderPara): MyStructListBuilder<MyStruct>() {
    val area get() = para.area
    val nextToWalls get() = para.nextToWalls
    val nextToSplitter get() = para.nextToSplitter
    val openRange = OpenRangeBuilder(area,para.scope.rand) {it !in nextToWalls}.build()

    open fun getLeafWalls():List<LeafWall> = mutableListOf<LeafWall>().also{ list ->
        Direction2D.entries.forEach { d ->
            mutableListOf(d.run { area.w.first+1..area.w.last }).apply {
                openRange.forEach { if(it.first == d){
                    val range = it.second
                    val toSplit = first { range overlap it }
                    val s1 = toSplit.first..range.first - 1
                    val s2 = range.last + 1..toSplit.last
                    remove(toSplit)
                    add(s1)
                    add(s2)
                } }
            }.forEach {
                val wallArea = d.run { area2D(area.bound(this).range,it) }
                list += LeafWallBuilder(d.left, wallArea).build()
            }
        }
    }
    open fun StructureBuildScope.getLights():List<StreetLight> {
        val light = mutableListOf<StreetLightBuilder>()
        val filterAreas = openRange.map { it.toOpenArea(area).offset(it.first,-1) }
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