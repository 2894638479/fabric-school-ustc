package org.schoolustc.structureDsl

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.RandomSource
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.levelgen.structure.StructurePiece
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType

abstract class MyStruct (
    val info:MyStructInfo<*>,
    val config:StructGenConfig,
    area: Area? = null
): StructurePiece(info.type,0,(
        info.area ?: area ?: error("unknown struct area")
    ).boundingBox(config)) {
    final override fun postProcess(
        worldGenLevel: WorldGenLevel,
        structureManager: StructureManager,
        chunkGenerator: ChunkGenerator,
        randomSource: RandomSource,
        boundingBox: BoundingBox,
        chunkPos: ChunkPos,
        blockPos: BlockPos
    ) {
        info.buildAsT(this,
            StructBuilder(
                worldGenLevel,
                config,
                randomSource
            )
        )
    }

    final override fun addAdditionalSaveData(
        structurePieceSerializationContext: StructurePieceSerializationContext,
        compoundTag: CompoundTag
    ) { info.saveAsT(this,compoundTag) }

    final override fun getType(): StructurePieceType = info.type
}