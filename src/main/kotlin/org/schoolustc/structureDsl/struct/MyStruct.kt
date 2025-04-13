package org.schoolustc.structureDsl.struct

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
import org.schoolustc.Profiler
import org.schoolustc.structureDsl.Area
import org.schoolustc.structureDsl.struct.scope.StructBuildScope

abstract class MyStruct (
    val info: MyStructInfo<*>,
    val box: Area
): StructurePiece(info.type,0,box.checkNotEmpty().toBoundingBox()) {
    open val profileName get() = "structure ${info.id}"
    open val profileTimeOutMs get() = 500L
    override fun postProcess(
        worldGenLevel: WorldGenLevel,
        structureManager: StructureManager,
        chunkGenerator: ChunkGenerator,
        randomSource: RandomSource,
        boundingBox: BoundingBox,
        chunkPos: ChunkPos,
        blockPos: BlockPos
    ) {
        Profiler.task(profileName,profileTimeOutMs){
            StructBuildScope(worldGenLevel,randomSource,boundingBox,chunkGenerator).build()
        }
    }
    abstract fun StructBuildScope.build()

    final override fun addAdditionalSaveData(
        structurePieceSerializationContext: StructurePieceSerializationContext,
        compoundTag: CompoundTag
    ) { info.saveAsT(this,compoundTag) }

    final override fun getType(): StructurePieceType = info.type
}