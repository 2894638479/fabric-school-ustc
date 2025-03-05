package org.schoolustc.structureDsl.struct

import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.*
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.schoolustc.fullId
import org.schoolustc.interfaces.palettes
import org.schoolustc.logger
import org.schoolustc.structureDsl.*

class StructBuilder(
    val world:WorldGenLevel,
    val config: StructGenConfig,
    val rand:RandomSource
) {
    inline val Point.finalPos get() = finalPos(config)
    inline val Point.finalSurfacePos get() = finalSurfacePos(config) { x, z ->
        world.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z) - 1
    }
    private fun setBlock(finalPos: Point, state:BlockState) = world.setBlock(finalPos.blockPos,state,3)
    private infix fun BlockState.setTo(finalPos: Point) = setBlock(finalPos,this)
    private inline val Block.state get() = defaultBlockState()


    infix fun Selector<Block>.fill(pos: Point) = select().state setTo pos.finalPos
    infix fun Selector<Block>.fill(area: AreaProg) = area.iterate { fill(it) }
    infix fun BlockState.fill(pos:Point) = this setTo pos.finalPos
    infix fun BlockState.fill(area:AreaProg) = area.iterate { this fill it }
    infix fun Block.fill(pos: Point) = state setTo pos.finalPos
    infix fun Block.fill(area: AreaProg) = area.iterate { fill(it) }
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
    infix fun Block.fillX(area: Area) = area.iterate { this fillX it }
    infix fun Block.fillX(pos: Point) = connectedState(config.rotate) setTo pos.finalPos
    infix fun Block.fillZ(area: Area) = area.iterate { this fillZ it }
    infix fun Block.fillZ(pos: Point) = connectedState(!config.rotate) setTo pos.finalPos
    infix fun Block.fillXS(area: Area) = area.iterate { this fillXS it }
    infix fun Block.fillXS(pos: Point) = connectedState(config.rotate) setTo pos.finalSurfacePos
    infix fun Block.fillZS(area: Area) = area.iterate { this fillZS it }
    infix fun Block.fillZS(pos: Point) = connectedState(!config.rotate) setTo pos.finalSurfacePos
    private fun Block.connectedState(finalPos: Point):BlockState{
        var state = state
        fun connect(pos: BlockPos,prop:BooleanProperty){
            if(!world.getBlockState(pos).isAir){
                state = state.setValue(prop,true)
            }
        }
        val p = finalPos.blockPos
        connect(p.west(),WEST)
        connect(p.east(), EAST)
        connect(p.south(), SOUTH)
        connect(p.north(), NORTH)
        return state
    }
    //true为x轴方向连接，false为z轴方向连接
    private fun Block.connectedState(rotate:Boolean) = state
        .setValue(if(rotate) NORTH else WEST,true)
        .setValue(if(rotate) SOUTH else EAST,true)
    //将y轴转化为相对于世界表面的坐标
    infix fun Block.fillS(area: AreaProg) = area.iterate { state setTo it.finalSurfacePos }
    infix fun Block.fillS(pos: Point) = state setTo pos.finalSurfacePos

    fun <T> selector(map:Map<T,Float>): Selector<T> {
        val sum = map.values.sum()
        return Selector {
            val r = rand.nextFloat() * sum
            var f = 0f
            for((block,weight) in map){
                f += weight
                if(r < f) return@Selector block
            }
            return@Selector map.keys.last()
        }
    }

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
    inline val randBool get() = randBool(0.5f)
}



