package org.schoolustc

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.StructurePiece
import net.minecraft.world.level.levelgen.structure.StructureType
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType
import org.schoolustc.SchoolUSTC.id
import java.util.*


fun CompoundTag.putPos(pos:BlockPos){
    putInt("X",pos.x)
    putInt("Y",pos.y)
    putInt("Z",pos.z)
}
fun CompoundTag.getPos():BlockPos = BlockPos(getInt("X"),getInt("Y"),getInt("Z"))

class DiamondStructure(settings:StructureSettings): Structure(settings) {
    companion object {
        val CODEC = RecordCodecBuilder.create {
            it.group(
                settingsCodec(it)
            ).apply(it,::DiamondStructure)
        }
        fun register(){
            Registry.register(BuiltInRegistries.STRUCTURE_TYPE, ResourceLocation(id,"diamond_structure"), type )
        }
        val type = StructureType { CODEC }
    }
    override fun findGenerationPoint(context: GenerationContext): Optional<GenerationStub> {
        val chunkPos = context.chunkPos
        val x = chunkPos.middleBlockX
        val z = chunkPos.middleBlockZ
        val y = context.chunkGenerator.getFirstFreeHeight(
            x,z,
            Heightmap.Types.WORLD_SURFACE_WG,
            context.heightAccessor,
            context.randomState
        )
        val pos = BlockPos(x,y,z)
        return Optional.of(
            GenerationStub(BlockPos(x,y,z)){
                it.addPiece(DiamondStructurePiece(pos))
            }
        )
    }

    override fun type(): StructureType<*> = type
}

class DiamondStructurePiece(val pos: BlockPos) : StructurePiece(
    ::DiamondStructurePiece,
    0,
    BoundingBox(pos.x - 2, pos.y - 2, pos.z - 2, pos.x + 2, pos.y + 2, pos.z + 2)
) {
    constructor(context: StructurePieceSerializationContext,compoundTag: CompoundTag):this(compoundTag.getPos())
    companion object {
        fun register(){
            Registry.register(BuiltInRegistries.STRUCTURE_PIECE,ResourceLocation(id,"diamond_structure_piece"), type)
        }
        val type = StructurePieceType(::DiamondStructurePiece)
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
        for (x in -2..2) {
            for (y in -2..2) {
                for (z in -2..2) {
                    worldGenLevel.setBlock(pos.offset(x,y,z),Blocks.DIAMOND_BLOCK.defaultBlockState(),3)
                }
            }
        }
    }

    override fun getType(): StructurePieceType = Companion.type
}

