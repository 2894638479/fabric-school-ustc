package org.schoolustc.structureDsl.struct

import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.schoolustc.Profiler
import org.schoolustc.structureDsl.Area
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.struct.scope.StructBuildScope
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

abstract class MyStructWithConfig(
    info: MyStructInfo<*>,
    size:Point,
    val config:StructGenConfig,
):MyStruct(info,Area(
    0..<size.x,
    0..<size.y,
    0..<size.z,
).applyConfig(config)){
    final override fun postProcess(
        worldGenLevel: WorldGenLevel,
        structureManager: StructureManager,
        chunkGenerator: ChunkGenerator,
        randomSource: RandomSource,
        boundingBox: BoundingBox,
        chunkPos: ChunkPos,
        blockPos: BlockPos
    ) {
        Profiler.task(info.profileName,info.profileTimeOutMs){
            StructBuildScopeWithConfig(config,worldGenLevel,customRandomSource ?: randomSource,boundingBox,chunkGenerator).build()
        }
    }
    final override fun StructBuildScope.build() {}
    abstract fun StructBuildScopeWithConfig.build()
}