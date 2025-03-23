package org.schoolustc.structureDsl.structure

import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.nextBool
import org.schoolustc.structureDsl.nextInt
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structs.*
import kotlin.math.log2

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
    fun randArea(posX:Int,posZ:Int,size:IntProgression): Area2D {
        val width = rand.nextInt(size)
        val height = rand.nextInt(size)
        val x = posX - width / 2
        val z = posZ - height / 2
        return Area2D(x,z,width,height)
    }
}