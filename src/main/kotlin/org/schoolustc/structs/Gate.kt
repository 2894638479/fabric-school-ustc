package org.schoolustc.structs

import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import net.minecraft.world.level.block.StairBlock
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
    override fun StructBuildScope.build() {
        CALCITE fill Area(2..3,0..7,1..2)
        CALCITE fill Area(11..12,0..7,1..2)
        CALCITE fill Area(4..10,6..7,1..2)
        CALCITE fill Area(1..1,7..7,1..2)
        CALCITE fill Area(13..13,6..7,1..2)
        fun state(facing:Direction2D,shape: StairsShape = StairsShape.STRAIGHT) =
            POLISHED_DIORITE_STAIRS.stairState(facing,shape,Half.TOP)
        state(Direction2D.Z2) fill Area(1..13,7..7,0..0)
        state(Direction2D.Z1) fill Area(1..13,7..7,3..3)
        state(Direction2D.X2) fill Area(0..0,7..7,1..2)
        state(Direction2D.X1) fill Area(14..14,7..7,1..2)
        state(Direction2D.X2) fill Area(1..1,6..6,1..2)
        state(Direction2D.X1) fill Area(13..13,6..6,1..2)
        state(Direction2D.X2,StairsShape.OUTER_RIGHT) fill Point(0,7,0)
        state(Direction2D.X2,StairsShape.OUTER_LEFT) fill Point(0,7,3)
        state(Direction2D.X1,StairsShape.OUTER_RIGHT) fill Point(14,7,3)
        state(Direction2D.X1,StairsShape.OUTER_LEFT) fill Point(14,7,0)
    }
}