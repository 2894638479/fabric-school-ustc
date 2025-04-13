package org.schoolustc.structureDsl.struct.scope

import net.minecraft.util.RandomSource
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.Point

class StructBuildScopeWithConfig(
    val config: StructGenConfig,
    world: WorldGenLevel,
    rand: RandomSource,
    boundingBox: BoundingBox,
    chunkGenerator: ChunkGenerator
):StructBuildScope(world, rand, boundingBox, chunkGenerator) {
    class RelativeView(scope: StructBuildScopeWithConfig): View(scope){
        val config = scope.config
        override fun Point.finalXZ() = finalXZ(config)
        override fun Point.finalY() = Point(x,y + config.pos.y, z)
        override fun Direction2D.final() = applyConfig(config)
        override fun Area2D.final() = applyConfig(config)
    }
    class SurfView(scope: StructBuildScopeWithConfig): View(scope){
        val config = scope.config
        override fun Point.finalXZ() = finalXZ(config)
        override fun Point.finalY() = Point(x,y + surfHeight(x,z),z)
        override fun Direction2D.final() = applyConfig(config)
        override fun Area2D.final() = applyConfig(config)
    }
    inline fun inRelativeView(task: RelativeView.()->Unit) = RelativeView(this).task()
    inline fun inSurfView(task: SurfView.()->Unit) = SurfView(this).task()
}