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
import org.schoolustc.SchoolUSTC.logger
import org.schoolustc.structure.classroom
import org.schoolustc.tools.StructureGenerator
import org.schoolustc.tools.getPos
import org.schoolustc.tools.putPos
import org.schoolustc.tools.rand

class ClassroomPiece(
    val pos: BlockPos,
    val seed:Int
) : StructurePiece(
    ::ClassroomPiece,
    0,
    BoundingBox(pos.x - 2, pos.y - 2, pos.z - 2, pos.x + 2, pos.y + 2, pos.z + 2)
) {
    constructor(context: StructurePieceSerializationContext, compoundTag: CompoundTag):this(
        compoundTag.getPos(),
        compoundTag.getInt("S")
    )
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
        compoundTag.putInt("S",seed)
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
        classroom(
            seed.rand(8..10,23487),
            seed.rand(4..5,123890),
            seed.rand(12..15,7868),
        ).generate(
            worldGenLevel,pos,
            seed and 1 != 0,
            seed and 2 != 0,
            seed and 4 != 0
        )
    }

    override fun getType(): StructurePieceType = Companion.type
}