package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.SMOOTH_STONE
import net.minecraft.world.level.block.Blocks.SMOOTH_STONE_SLAB
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class StairwellTop(config: StructGenConfig):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<StairwellTop>(
        "stairwell_top",
        Point(11,2,7)
    ){
        override val defaultDirection = Direction2D.ZPlus
        override fun StairwellTop.saveTag(tag: CompoundTag) {
            tag.write("C",config)
        }
        override fun loadTag(tag: CompoundTag) = StairwellTop(tag.read("C"))
    }

    override fun StructBuildScopeWithConfig.build() {
        inRelativeView {
            SMOOTH_STONE fill Area(xRange,0.range,zRange)
            SMOOTH_STONE_SLAB fill Area(0.range,1.range,zRange)
            SMOOTH_STONE_SLAB fill Area(xMax.range,1.range,zRange)
            SMOOTH_STONE_SLAB fill Area(xRange.padding(1),1.range,0.range)
            SMOOTH_STONE_SLAB fill Area(4..xMax,1.range,zMax.range)
        }
    }
}