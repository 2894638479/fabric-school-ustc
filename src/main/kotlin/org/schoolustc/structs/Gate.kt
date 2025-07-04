package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.level.block.state.properties.StairsShape
import org.schoolustc.items.CARD_MACHINE_BLOCK
import org.schoolustc.items.SCHOOL_FENCE_GATE_BLOCK
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.*
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class Gate(config: StructGenConfig):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<Gate>("gate",Point(15,8,4)){
        override val defaultDirection = Direction2D.ZPlus
        override fun loadTag(tag: CompoundTag) = Gate(tag.read("C"))
        override fun Gate.saveTag(tag: CompoundTag) = tag.write("C",config)
    }
    override fun StructBuildScopeWithConfig.build() {
        inRelativeView {
            CALCITE fillUnder Area(2..3, 8..8, 1..2)
            CALCITE fillUnder Area(11..12, 8..8, 1..2)
            CALCITE fill Area(4..10, 6..7, 1..2)
            CALCITE fill Area(1..1, 7..7, 1..2)
            CALCITE fill Area(13..13, 6..7, 1..2)
            fun state(facing: Direction2D, shape: StairsShape = StairsShape.STRAIGHT) =
                POLISHED_DIORITE_STAIRS.stairState(facing, shape, Half.TOP)
            state(Direction2D.ZPlus) fill Area(1..13, 7..7, 0..0)
            state(Direction2D.ZMin) fill Area(1..13, 7..7, 3..3)
            state(Direction2D.XPlus) fill Area(0..0, 7..7, 1..2)
            state(Direction2D.XMin) fill Area(14..14, 7..7, 1..2)
            state(Direction2D.XPlus) fill Area(1..1, 6..6, 1..2)
            state(Direction2D.XMin) fill Area(13..13, 6..6, 1..2)
            state(Direction2D.XPlus, StairsShape.OUTER_RIGHT) fill Point(0, 7, 0)
            state(Direction2D.XPlus, StairsShape.OUTER_LEFT) fill Point(0, 7, 3)
            state(Direction2D.XMin, StairsShape.OUTER_RIGHT) fill Point(14, 7, 3)
            state(Direction2D.XMin, StairsShape.OUTER_LEFT) fill Point(14, 7, 0)
        }

        inSurfView {
            val fence = SCHOOL_FENCE_GATE_BLOCK.state.setHorizontalDirection(Direction2D.ZPlus)
            val machine = CARD_MACHINE_BLOCK.state.setHorizontalDirection(Direction2D.ZPlus)
            fence fill Area(4..10,1.range,1.range)
            machine fill Point(7,1,1)
        }
    }
}