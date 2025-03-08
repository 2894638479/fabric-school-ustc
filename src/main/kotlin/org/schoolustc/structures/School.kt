package org.schoolustc.structures

import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.structure.MyStructure
import org.schoolustc.structureDsl.structure.MyStructureInfo
import org.schoolustc.structureDsl.structure.StructureBuilder

class School(settings:StructureSettings): MyStructure(Companion,settings) {
    companion object : MyStructureInfo<School>("school",::School)

    override fun StructureBuilder.build(pos:Point) {
        splitArea(randArea(pos.x,pos.z,50..80))
        wallArea.forEach { it.toWall().add() }
        wallCorArea.forEach { it.toWallCor().add() }
        streetArea.forEach { it.toStreet().add() }
        roadArea.forEach { it.toRoad().add() }
        splitterArea.forEach { it.toSplitter().add() }
        gateArea.forEach { it.toGate().add() }
    }

    override fun GenerationContext.findPoint() = Point(chunkPos.middleBlockX,100,chunkPos.middleBlockZ)
}