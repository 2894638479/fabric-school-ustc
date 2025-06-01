package org.schoolustc.structureDsl.struct.scope

import net.minecraft.util.RandomSource
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.schoolustc.structureDsl.*


open class StructBuildScope(
    val world:WorldGenLevel,
    val rand:RandomSource,
    val boundingBox: BoundingBox,
    val chunkGenerator: ChunkGenerator
) {
    class RawView(scope: StructBuildScope): View(scope){
        override fun Point.finalXZ() = this
        override fun Point.finalY() = this
        override fun Direction2D.final() = this
        override fun Area2D.final() = this
        override val mirror get() = false
    }
    class RawSurfView(scope: StructBuildScope): View(scope){
        override fun Point.finalXZ() = this
        override fun Point.finalY() = Point(x,y + surfHeight(x,z),z)
        override fun Direction2D.final() = this
        override fun Area2D.final() = this
        override val mirror get() = false
    }

    inline fun inRawView(task: RawView.()->Unit) = RawView(this).task()
    inline fun inRawSurfView(task: RawSurfView.()->Unit) = RawSurfView(this).task()
}



