package org.schoolustc.structures

import org.schoolustc.structs.listBuilser.ScaffoldBuilder
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.structure.MyStructure
import org.schoolustc.structureDsl.structure.MyStructureInfo
import org.schoolustc.structureDsl.structure.StructureBuildScope

class School(settings:StructureSettings): MyStructure(Companion,settings) {
    companion object : MyStructureInfo<School>("school",::School)

    override fun StructureBuildScope.build(pos:Point) {
        val area = randArea(pos.x,pos.z,50..80)
        try {
            ScaffoldBuilder(area).run { this@build.build() }.addToPieces()
        } catch (e:Exception){e.printStackTrace()}
    }

    override fun GenerationContext.findPoint() = Point(chunkPos.middleBlockX,100,chunkPos.middleBlockZ)
}