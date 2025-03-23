package org.schoolustc.structs

import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import net.minecraft.world.level.block.StairBlock.FACING
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.level.block.state.properties.StairsShape
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.*
import org.schoolustc.structureDsl.structure.MyStructFixedAreaInfo

class Gate(config:StructGenConfig):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedAreaInfo<Gate>("gate",Point(15,8,4)){
        override val defaultDirection = Direction2D.Z1
        override fun loadTag(tag: CompoundTag) = Gate(tag.getConfig())
        override fun Gate.saveTag(tag: CompoundTag) = tag.putConfig(config)
    }
    private fun BlockState.convert():BlockState{
        if(!config.rotate) return this
        val dir = getValue(FACING)
        return setValue(FACING,when(dir){
            Direction.EAST -> Direction.SOUTH
            Direction.SOUTH -> Direction.EAST
            Direction.WEST -> Direction.NORTH
            Direction.NORTH -> Direction.WEST
            else -> error("unknown direction $dir")
        })
    }
    override fun StructBuildScope.build() {
        CALCITE fill Area(2..3,0..7,1..2)
        CALCITE fill Area(11..12,0..7,1..2)
        CALCITE fill Area(4..10,6..7,1..2)
        CALCITE fill Area(1..1,7..7,1..2)
        CALCITE fill Area(13..13,6..7,1..2)
        val state = POLISHED_DIORITE_STAIRS.defaultBlockState()
            .setValue(BlockStateProperties.HALF,Half.TOP)
        fun state(facing:Direction,shape: StairsShape = StairsShape.STRAIGHT)
        = state.setValue(FACING,facing).setValue(BlockStateProperties.STAIRS_SHAPE,shape).convert()
        state(Direction.SOUTH) fill Area(1..13,7..7,0..0)
        state(Direction.NORTH) fill Area(1..13,7..7,3..3)
        state(Direction.EAST) fill Area(0..0,7..7,1..2)
        state(Direction.WEST) fill Area(14..14,7..7,1..2)
        state(Direction.EAST) fill Area(1..1,6..6,1..2)
        state(Direction.WEST) fill Area(13..13,6..6,1..2)
        state(Direction.EAST,StairsShape.OUTER_RIGHT) fill Point(0,7,0)
        state(Direction.EAST,StairsShape.OUTER_LEFT) fill Point(0,7,3)
        state(Direction.WEST,StairsShape.OUTER_RIGHT) fill Point(14,7,3)
        state(Direction.WEST,StairsShape.OUTER_LEFT) fill Point(14,7,0)
    }
}