package org.schoolustc.structureDsl.structure

import net.minecraft.util.RandomSource
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.nextInt
import org.schoolustc.structureDsl.struct.MyStruct

class StructureBuildScope(
    private val context:GenerationContext,
    private val builder:StructurePiecesBuilder
) {
    fun List<MyStruct>.addToPieces() = forEach { builder.addPiece(it) }
    val rand get() = context.random
    fun y(x:Int,z:Int) = context.chunkGenerator.getBaseHeight(
        x,z,
        Heightmap.Types.WORLD_SURFACE_WG,
        context.heightAccessor,
        context.randomState
    ) - 1
    val Area2D.minY get() = listOf(y(x1,z1),y(x2,z2),y(x1,z2),y(x2,z1)).min()
    val Area2D.avgY get() = listOf(y(x1,z1),y(x2,z2),y(x1,z2),y(x2,z1)).average()
}