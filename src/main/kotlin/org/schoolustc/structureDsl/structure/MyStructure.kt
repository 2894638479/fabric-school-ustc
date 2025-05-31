package org.schoolustc.structureDsl.structure

import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.StructureType
import org.schoolustc.structureDsl.Point
import java.util.*

abstract class MyStructure(
    val info : MyStructureInfo<*>,
    val settings:StructureSettings
) : Structure(settings) {
    abstract fun StructureBuildScope.build(pos:Point)
    abstract fun GenerationContext.findPoint():Point?
    final override fun type(): StructureType<*> = info.type
    final override fun findGenerationPoint(generationContext: GenerationContext): Optional<GenerationStub> {
        val point = generationContext.findPoint() ?: return Optional.empty()
        return Optional.of(
            GenerationStub(point.blockPos){
                StructureBuildScope(generationContext,it).build(point)
            }
        )
    }
}