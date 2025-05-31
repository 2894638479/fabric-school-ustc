package org.schoolustc

import net.minecraft.util.RandomSource
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.LegacyRandomSource
import org.schoolustc.structs.listbuilder.ScaffoldBuilder
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.nextInt
import org.schoolustc.structureDsl.structure.MyStructure
import org.schoolustc.structureDsl.structure.MyStructureInfo
import org.schoolustc.structureDsl.structure.StructureBuildScope


class School(settings:StructureSettings): MyStructure(Companion,settings) {
    companion object : MyStructureInfo<School>("school",::School){
        val Point.randomSource get() = LegacyRandomSource(hashCode().toLong())
        fun RandomSource.randArea(posX:Int, posZ:Int, size:IntProgression): Area2D {
            val width = nextInt(size)
            val height = nextInt(size)
            val x = posX - width / 2
            val z = posZ - height / 2
            return Area2D(x,z,width,height)
        }
        val Point.randArea get() = randomSource.randArea(x,z,108..143)
    }

    override fun StructureBuildScope.build(pos:Point) {
        val area = pos.randArea
        Profiler.task("build school",5000) {
            try {
                ScaffoldBuilder(area).run { this@build.build() }.addToPieces()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun GenerationContext.findPoint():Point? {
        val point = Point(chunkPos.middleBlockX, 100, chunkPos.middleBlockZ)
        val area = point.randArea
        val biome = biomeSource.getNoiseBiome(point.x shr 2, 0, point.z shr 2, randomState.sampler())
        if(!validBiome.test(biome)) return null

        fun y(x:Int,z:Int):Int = chunkGenerator.getBaseHeight(x,z,
            Heightmap.Types.WORLD_SURFACE_WG, heightAccessor(), randomState())
        class XZ (val x:Int,val z:Int){
            val y get() = y(x,z)
        }
        val sequence = sequence {
            for (i in area.x step 10){
                for (j in area.z step 10){
                    yield(XZ(i,j))
                }
            }
        }
        val list = sequence.toList()
        val minY = list.minOf { it.y }
        val maxY = list.maxOf { it.y }
        logger.info("$maxY $minY")
        val result = point.takeIf { maxY - minY < 30 }
        logger.info(result.toString())
        return result
    }
}