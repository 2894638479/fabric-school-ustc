package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.SMOOTH_STONE_SLAB
import net.minecraft.world.level.block.state.properties.SlabType
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class BalconyTop(config:StructGenConfig):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<BalconyTop>("balcony_top", Point(5,2,2)){
        override val defaultDirection = Direction2D.ZPlus
        override fun loadTag(tag: CompoundTag) = BalconyTop(tag.read("C"))
        override fun BalconyTop.saveTag(tag: CompoundTag) {
            tag.write("C",config)
        }
    }

    override fun StructBuildScopeWithConfig.build() = inRelativeView {
        SMOOTH_STONE_SLAB.slabState(SlabType.TOP) fill Area(Balcony.xRange,0.range, Balcony.zRange)
        val bound = SMOOTH_STONE_SLAB.slabState(SlabType.BOTTOM)
        bound fill Area(Balcony.xRange,1.range, 1.range)
        bound fill Point(0,1,0)
        bound fill Point(xMax,1,0)
    }
}