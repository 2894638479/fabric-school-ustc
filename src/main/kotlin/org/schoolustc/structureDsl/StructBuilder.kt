package org.schoolustc.structureDsl

import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty

class StructBuilder(
    val world:WorldGenLevel,
    val config:StructGenConfig,
    val rand:RandomSource
) {
    infix fun BlockSelector.fill(pos: Point) = world.setBlock(pos.finalPos(config).blockPos,select(),3)
    infix fun BlockSelector.fill(area: Area) = area.iterate { fill(it) }
    infix fun Block.fill(pos: Point) = world.setBlock(pos.finalPos(config).blockPos,defaultBlockState(),3)
    infix fun Block.fill(area: Area) = area.iterate { fill(it) }
    infix fun BlockSelector.fillWall(area: Area){
        val p1 = area.getP1()
        val p2 = area.getP2()
        this fill Area(p1.x..p1.x,p1.y..p2.y,p1.z..p2.z)
        this fill Area(p2.x..p2.x,p1.y..p2.y,p1.z..p2.z)
        this fill Area(p1.x+1 ..p2.x-1,p1.y..p2.y,p1.z..p1.z)
        this fill Area(p1.x+1 ..p2.x-1,p1.y..p2.y,p2.z..p2.z)
    }
    infix fun Block.fillWall(area: Area) = BlockSelector { defaultBlockState() } fillWall area
    infix fun Block.fillConnectable(area: Area) = area.iterate {
        var state = defaultBlockState()
        fun connect(pos: BlockPos,prop:BooleanProperty){
            if(!world.getBlockState(pos).isAir){
                state = state.setValue(prop,true)
            }
        }
        val pos = it.finalPos(config).blockPos
        connect(pos.west(),BlockStateProperties.WEST)
        connect(pos.east(),BlockStateProperties.EAST)
        connect(pos.south(),BlockStateProperties.SOUTH)
        connect(pos.north(),BlockStateProperties.NORTH)
        world.setBlock(pos,state,3)
    }

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



