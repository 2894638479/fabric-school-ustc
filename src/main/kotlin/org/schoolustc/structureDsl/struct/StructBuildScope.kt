package org.schoolustc.structureDsl.struct

import net.minecraft.util.RandomSource
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.*
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.level.block.state.properties.StairsShape
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.schoolustc.fullId
import org.schoolustc.interfaces.palettes
import org.schoolustc.logger
import org.schoolustc.structureDsl.*

class StructBuildScope(
    val world:WorldGenLevel,
    val config: StructGenConfig,
    val rand:RandomSource
) {
    inline val Point.finalPos get() = finalPos(config)
    inline val Point.finalSurfacePos get() = finalPos.toSurface { x, z ->
        world.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z) - 1
    }
    private fun setBlock(finalPos: Point, state:BlockState) = world.setBlock(finalPos.blockPos,state,3)
    private infix fun BlockState.setTo(finalPos: Point) = setBlock(finalPos,this)
    private inline val Block.state get() = defaultBlockState()

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
    //填充并处理玻璃板等连接
    infix fun Block.fillX(fillable: Fillable) = fillable.fill { connectedState(config.rotate) setTo it.finalPos }
    infix fun Block.fillZ(fillable: Fillable) = fillable.fill { connectedState(!config.rotate) setTo it.finalPos }
    infix fun Block.fillXS(fillable: Fillable) = fillable.fill { connectedState(config.rotate) setTo it.finalSurfacePos }
    infix fun Block.fillZS(fillable: Fillable) = fillable.fill { connectedState(!config.rotate) setTo it.finalSurfacePos }
    //true为x轴方向连接，false为z轴方向连接
    private fun Block.connectedState(rotate:Boolean) = state
        .setValue(if(rotate) NORTH else WEST,true)
        .setValue(if(rotate) SOUTH else EAST,true)
    //将y轴转化为相对于世界表面的坐标
    infix fun Block.fillS(fillable: Fillable) = fillable.fill { state setTo it.finalSurfacePos }
    infix fun Selector<BlockState>.fillS(fillable: Fillable) = fillable.fill { select() setTo it.finalSurfacePos }

    private fun getNbtStruct(name:String):StructureTemplate?{
        return (world.server ?: return null)
            .structureManager
            .get(fullId(name))
            .orElse(null)
    }
    private fun putNbtStruct(name:String, startPos: Point, filterAir:Boolean){
        val struct = getNbtStruct(name) ?: return logger.warn("not found structure nbt $name")
        struct.palettes.run {
            getOrNull(rand.nextInt(size)) ?: return logger.warn("empty palette")
        }.blocks().forEach {
            if(!(filterAir && it.state.isAir))
                world.setBlock(it.pos.point.plus(startPos).finalPos.blockPos,it.state,3)
        }
    }
    //放置nbt
    infix fun String.put(startPos: Point) = putNbtStruct(this,startPos,false)
    //过滤空气
    infix fun String.putF(startPos: Point) = putNbtStruct(this,startPos,true)

    fun randBool(trueChance:Float) = rand.nextFloat() < trueChance
    fun Block.stairState(facing:Direction2D,shape:StairsShape = StairsShape.STRAIGHT,half: Half = Half.BOTTOM) =
        state
            .setValue(StairBlock.FACING,facing.applyConfig(config).toMcDirection())
            .setValue(STAIRS_SHAPE,shape)
            .setValue(HALF,half)
    fun Block.leafState(persist:Boolean) = state.setValue(PERSISTENT,persist)
}



