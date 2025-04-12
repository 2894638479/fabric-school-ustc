package org.schoolustc

import org.schoolustc.structs.listbuilder.ScaffoldBuilder
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.structure.MyStructure
import org.schoolustc.structureDsl.structure.MyStructureInfo
import org.schoolustc.structureDsl.structure.StructureBuildScope

class School(settings:StructureSettings): MyStructure(Companion,settings) {
    companion object : MyStructureInfo<School>("school",::School)

    override fun StructureBuildScope.build(pos:Point) {
        val area = randArea(pos.x,pos.z,108..143)
        Profiler.task("build school",5000) {
            try {
                ScaffoldBuilder(area).run { this@build.build() }.addToPieces()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun GenerationContext.findPoint() = Point(chunkPos.middleBlockX,100,chunkPos.middleBlockZ)
}