package org.schoolustc.tools

import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents.Register
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.RandomSource
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.levelgen.structure.StructurePiece
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType
import org.schoolustc.SchoolUSTC.logger
import kotlin.math.max
import kotlin.math.min

class Area(
    val x:IntRange,
    val y:IntRange,
    val z:IntRange
) {
    inline fun iterate(block:(Point) -> Unit){
        for(i in x){for (j in y){for (k in z){
            block(Point(i,j,k))
        }}}
    }
    fun boundingBox(config: StructGenConfig):BoundingBox{
        val p1 = Point(x.first,y.first,z.first).finalPos(config)
        val p2 = Point(x.last,y.last,z.last).finalPos(config)
        return BoundingBox(
            min(p1.x,p2.x),
            min(p1.y,p2.y),
            min(p1.z,p2.z),
            max(p1.x,p2.x),
            max(p1.y,p2.y),
            max(p1.z,p2.z),
        )
    }
}
data class Point(
    val x:Int,
    val y:Int,
    val z:Int
){
    val blockPos get() = BlockPos(x,y,z)
    fun finalPos(config: StructGenConfig):Point{
        val xAdd = if (config.revX) - x else x
        val zAdd = if (config.revZ) - z else z
        return Point(
            config.pos.x + if (config.rotate) zAdd else xAdd,
            config.pos.y + y,
            config.pos.z + if (config.rotate) xAdd else zAdd
        )
    }
}
val BlockPos.point get() = Point(x,y,z)
fun interface BlockSelector{
    fun select():BlockState
}
class StructBuilder(
    val world:WorldGenLevel,
    val config:StructGenConfig,
    val rand:RandomSource
) {
    private inline val Point.finalPos: Point get() = finalPos(config)
    infix fun BlockSelector.fill(pos: Point) = world.setBlock(pos.finalPos.blockPos,select(),3)
    infix fun BlockSelector.fill(area: Area) = area.iterate { fill(it) }
    infix fun Block.fill(pos: Point) = world.setBlock(pos.finalPos.blockPos,this.defaultBlockState(),3)
    infix fun Block.fill(area: Area) = area.iterate { fill(it) }
    infix fun BlockState.fill(pos: Point) = world.setBlock(pos.finalPos.blockPos,this,3)
    infix fun BlockState.fill(area: Area) = area.iterate { fill(it) }

    fun selector(map:Map<Block,Float>): BlockSelector{
        val sum = map.values.sum()
        return BlockSelector {
            val r = rand.nextFloat() * sum
            var f = 0f
            for((block,weight) in map){
                f += weight
                if(r < f) return@BlockSelector block.defaultBlockState()
            }
            return@BlockSelector map.keys.last().defaultBlockState()
        }
    }
}
class StructGenConfig(
    val pos: Point,
    val revX: Boolean,
    val revZ: Boolean,
    val rotate: Boolean
)
abstract class MyStructInfo <T:MyStruct>(
    val id:String,
    val area:Area
){
    abstract fun StructBuilder.build()
    abstract fun loadTag(tag: CompoundTag):T
    abstract fun T.saveTag(tag: CompoundTag)
    val type = StructurePieceType { _, tag -> loadTag(tag) }
    fun saveStructTag(s:MyStruct,tag : CompoundTag){
        val struct = s as? T
        struct?.saveTag(tag) ?: logger.error("type convert error:${s.javaClass.name}")
    }
    fun register() = Registry.register(BuiltInRegistries.STRUCTURE_PIECE,id,type)
}
abstract class MyStruct (
    val info:MyStructInfo<*>,
    val config:StructGenConfig
): StructurePiece(info.type,0,info.area.boundingBox(config)) {
    final override fun postProcess(
        worldGenLevel: WorldGenLevel,
        structureManager: StructureManager,
        chunkGenerator: ChunkGenerator,
        randomSource: RandomSource,
        boundingBox: BoundingBox,
        chunkPos: ChunkPos,
        blockPos: BlockPos
    ) {
        info.run {
            StructBuilder(
                worldGenLevel,
                config,
                randomSource
            ).build()
        }
    }

    final override fun addAdditionalSaveData(
        structurePieceSerializationContext: StructurePieceSerializationContext,
        compoundTag: CompoundTag
    ) { info.saveStructTag(this,compoundTag) }

    final override fun getType(): StructurePieceType = info.type
}
