package org.schoolustc.structures

import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structureDsl.structure.MyStructure
import org.schoolustc.structureDsl.structure.MyStructureInfo
import org.schoolustc.structureDsl.structure.StructureBuilder
import org.schoolustc.structurePieces.OuterWallPiece

class School(settings:StructureSettings): MyStructure(Companion,settings) {
    companion object : MyStructureInfo<School>("school",::School)

    override fun StructureBuilder.build(pos:Point) {
        OuterWallPiece(StructGenConfig(pos,randBool,randBool,randBool),12).add()
    }

    override fun GenerationContext.findPoint() = Point(chunkPos.middleBlockX,100,chunkPos.middleBlockZ)
}