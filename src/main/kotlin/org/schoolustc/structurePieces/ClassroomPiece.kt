package org.schoolustc.structurePieces

import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.levelgen.structure.StructurePiece
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType
import org.schoolustc.SchoolUSTC.id
import org.schoolustc.structure.classroom
import org.schoolustc.tools.StructureGenerator
import org.schoolustc.tools.getPos
import org.schoolustc.tools.putPos

class ClassroomPiece(
    val pos: BlockPos,
) : StructurePiece(
    ::ClassroomPiece,
    0,
    BoundingBox(pos.x - 2, pos.y - 2, pos.z - 2, pos.x + 2, pos.y + 2, pos.z + 2)
) {
    constructor(context: StructurePieceSerializationContext, compoundTag: CompoundTag):this(compoundTag.getPos())
    companion object {
        fun register(){
            Registry.register(BuiltInRegistries.STRUCTURE_PIECE, ResourceLocation(id,"classroom"), type)
        }
        val type = StructurePieceType(::ClassroomPiece)
    }
    override fun addAdditionalSaveData(
        structurePieceSerializationContext: StructurePieceSerializationContext,
        compoundTag: CompoundTag
    ) {
        compoundTag.putPos(pos)
    }

    override fun postProcess(
        worldGenLevel: WorldGenLevel,
        structureManager: StructureManager,
        chunkGenerator: ChunkGenerator,
        randomSource: RandomSource,
        boundingBox: BoundingBox,
        chunkPos: ChunkPos,
        blockPos: BlockPos
    ) {
        classroom.generate(worldGenLevel,pos,false,false,true)
    }

    override fun getType(): StructurePieceType = Companion.type
}