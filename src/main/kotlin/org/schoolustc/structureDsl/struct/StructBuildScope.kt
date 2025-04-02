package org.schoolustc.structureDsl.struct

import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.features.TreeFeatures
import net.minecraft.resources.ResourceKey
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks.CHEST
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.*
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.level.block.state.properties.StairsShape
import net.minecraft.world.level.block.state.properties.WallSide
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.schoolustc.fullId
import org.schoolustc.interfaces.palettes
import org.schoolustc.logger
import org.schoolustc.structureDsl.*

class StructBuildScope(
    val world:WorldGenLevel,
    val config: StructGenConfig,
    val rand:RandomSource,
    val chunkGenerator: ChunkGenerator
) {
    private inline val Point.finalPos get() = finalPos(config)
    private inline val Point.finalSurfacePos get() = finalSurfacePos(config){ x, z ->
        world.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z) - 1
    }
    private inline val Direction2D.finalDirection get() = applyConfig(config)
    private fun setBlock(finalPos: Point, state:BlockState) = world.setBlock(finalPos.blockPos,state,3)
    private infix fun BlockState.setTo(finalPos: Point.FinalPoint) = setBlock(finalPos,this)
    @JvmName("finalSurfacePosGetter") fun Point.getFinalSurfacePos() = finalSurfacePos
    inline val Block.state get() = defaultBlockState()

    infix fun Selector<Block>.fill(fillable: Fillable) = fillable.fill { select().state setTo it.finalPos }
    infix fun BlockState.fill(fillable: Fillable) = fillable.fill { this setTo it.finalPos }
    infix fun Block.fill(fillable: Fillable) = fillable.fill { state setTo it.finalPos }
    infix fun Selector<Block>.fillWall(area: Area){
        val p1 = area.getP1()
        val p2 = area.getP2()
        this fill Area(p1.x..p1.x,p1.y..p2.y,p1.z..p2.z)
        this fill Area(p2.x..p2.x,p1.y..p2.y,p1.z..p2.z)
        this fill Area(p1.x+1 ..p2.x-1,p1.y..p2.y,p1.z..p1.z)
        this fill Area(p1.x+1 ..p2.x-1,p1.y..p2.y,p2.z..p2.z)
    }
    infix fun Block.fillWall(area: Area) = Selector { this } fillWall area
    fun BlockState.connected(vararg direction:Direction2D):BlockState {
        var result = this
        for (d in direction){
            result = result.setValue(d.finalDirection.toMcProperty(),true)
        }
        return result
    }
    fun BlockState.connected(wallSide: WallSide,vararg direction:Direction2D):BlockState {
        var result = this
        for (d in direction){
            result = result.setValue(d.finalDirection.toMcWallProperty(),wallSide)
        }
        return result
    }
    inline val Block.connectedX get() = state.connected(Direction2D.XMin,Direction2D.XPlus)
    inline val Block.connectedZ get() = state.connected(Direction2D.ZMin,Direction2D.ZPlus)
    //将y轴转化为相对于世界表面的坐标
    infix fun Block.fillS(fillable: Fillable) = fillable.fill { state setTo it.finalSurfacePos }
    infix fun BlockState.fillS(fillable: Fillable) = fillable.fill { this setTo it.finalSurfacePos }
    infix fun Selector<BlockState>.fillS(fillable: Fillable) = fillable.fill { select() setTo it.finalSurfacePos }
    @JvmName("fillS1")
    infix fun Selector<Block>.fillS(fillable: Fillable) = fillable.fill { select().state setTo it.finalSurfacePos }

    private fun getNbtStruct(name:String):StructureTemplate?{
        return (world.server ?: return null)
            .structureManager
            .get(fullId(name))
            .orElse(null)
    }
    private fun putNbtStruct(name:String, startPos: Point, filterAir:Boolean){
        val struct = getNbtStruct(name) ?: return logger.warn("not found structure nbt $name")
        struct.palettes.forEach {
                it.blocks().forEach {
                if(!(filterAir && it.state.isAir)){
                    var state = it.state
                    if(it.state.hasProperty(Direction2D.XMin.toMcProperty())){
                        Direction2D.entries.forEach{ dir ->
                            state = state.setValue(dir.finalDirection.toMcProperty(),it.state.getValue(dir.toMcProperty()))
                        }
                    }
                    world.setBlock(it.pos.point.plus(startPos).finalPos.blockPos,state,3)
                }
            }
        }
    }
    //放置nbt
    infix fun String.put(startPos: Point) = putNbtStruct(this,startPos,true)
    //不过滤空气
    infix fun String.putA(startPos: Point) = putNbtStruct(this,startPos,false)

    fun randBool(trueChance:Float) = rand.nextFloat() < trueChance
    fun Block.stairState(facing:Direction2D,shape:StairsShape = StairsShape.STRAIGHT,half: Half = Half.BOTTOM) =
        state
            .setValue(StairBlock.FACING,facing.finalDirection.toMcDirection())
            .setValue(STAIRS_SHAPE,shape)
            .setValue(HALF,half)
    fun Block.leafState(persist:Boolean) = state.setValue(PERSISTENT,persist)

    infix fun ResourceKey<ConfiguredFeature<*, *>>.place(pos:Point)= world
        .level
        .registryAccess()
        .registryOrThrow(Registries.CONFIGURED_FEATURE)
        .getHolder(this)
        .orElse(null)
        ?.value()
        ?.place(
            world,
            chunkGenerator,
            rand,
            pos.finalPos.blockPos,
        )

    fun chest(
        pos:Point,
        facing:Direction2D,
        addItem:(Int)->ItemStack?
    ){
        CHEST.state.setValue(ChestBlock.FACING,facing.finalDirection.toMcDirection()) setTo pos.finalPos
        val entity = world.getBlockEntity(pos.finalPos.blockPos) as ChestBlockEntity
        for(i in 0..<entity.containerSize){
            addItem(i)?.let { entity.setItem(i,it) }
        }
    }
}



