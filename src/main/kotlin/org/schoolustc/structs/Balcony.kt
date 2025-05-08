package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.OAK_FENCE
import net.minecraft.world.level.block.Blocks.SMOOTH_STONE_SLAB
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.level.block.state.properties.SlabType
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class Balcony(config:StructGenConfig):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<Balcony>("balcony", Point(5,2,2)){
        override val defaultDirection = Direction2D.ZPlus
        override fun loadTag(tag: CompoundTag) = Balcony(tag.read("C"))
        override fun Balcony.saveTag(tag: CompoundTag) {
            tag.write("C",config)
        }
    }

    override fun StructBuildScopeWithConfig.build() = inRelativeView {
        SMOOTH_STONE_SLAB.slabState(SlabType.TOP) fill Area(xRange,0.range,zRange)
        val state1 = OAK_FENCE.state.connected(Direction2D.ZPlus,Direction2D.ZMin)
        state1 fill Point(0,1,0)
        state1 fill Point(4,1,0)
        OAK_FENCE.state.connected(Direction2D.ZMin,Direction2D.XPlus) fill Point(0,1,1)
        OAK_FENCE.state.connected(Direction2D.ZMin,Direction2D.XMin) fill Point(4,1,1)
        OAK_FENCE.state.connected(Direction2D.XPlus,Direction2D.XMin) fill Area(1..3,1.range,1.range)
    }
}