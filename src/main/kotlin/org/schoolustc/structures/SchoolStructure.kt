package org.schoolustc.structures

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.StructureType
import org.schoolustc.fullId
import org.schoolustc.structureDsl.StructGenConfig
import org.schoolustc.structureDsl.point
import org.schoolustc.structurePieces.ClassroomPiece
import org.schoolustc.structurePieces.OuterWallPiece
import org.schoolustc.structurePieces.StreetPiece
import java.util.*

class SchoolStructure(settings: StructureSettings): Structure(settings) {
    companion object {
        val CODEC = RecordCodecBuilder.create {
            it.group(
                settingsCodec(it)
            ).apply(it,::SchoolStructure)
        }
        fun register(){
            Registry.register(BuiltInRegistries.STRUCTURE_TYPE, fullId("school"), type)
        }
        val type = StructureType { CODEC }
    }
    override fun findGenerationPoint(context: GenerationContext): Optional<GenerationStub> {
        fun y(x:Int,z:Int) = context.chunkGenerator.getBaseHeight(
            x,z,
            Heightmap.Types.WORLD_SURFACE_WG,
            context.heightAccessor,
            context.randomState
        )
        val chunkPos = context.chunkPos
        val x = chunkPos.middleBlockX
        val z = chunkPos.middleBlockZ
        val y = y(x,z)
        val pos = BlockPos(x,y,z)
        return Optional.of(
            GenerationStub(BlockPos(x,y,z)){
                fun r() = context.random.nextFloat() > 0.5f
//                it.addPiece(ClassroomPiece(StructGenConfig(pos.point,r(),r(),r())))
                it.addPiece(OuterWallPiece(StructGenConfig(pos.point,r(),r(),r()),12))
            }
        )
    }

    override fun type(): StructureType<*> = type
}