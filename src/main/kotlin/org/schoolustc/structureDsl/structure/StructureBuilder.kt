package org.schoolustc.structureDsl.structure

import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder
import org.schoolustc.structureDsl.struct.MyStruct

class StructureBuilder(
    private val context:GenerationContext,
    private val builder:StructurePiecesBuilder
) {
    fun MyStruct.add() = builder.addPiece(this)
    fun y(x:Int,z:Int) = context.chunkGenerator.getBaseHeight(
        x,z,
        Heightmap.Types.WORLD_SURFACE_WG,
        context.heightAccessor,
        context.randomState
    ) - 1
    fun randBool(trueChance:Float) = context.random.nextFloat() < trueChance
    inline val randBool get() = randBool(0.5f)
}