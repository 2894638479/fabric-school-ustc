package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
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
        override val defaultDirection = Direction2D.ZMin
        override fun StairwellTop.saveTag(tag: CompoundTag) {
            tag.write("C",config)
        }
        override fun loadTag(tag: CompoundTag) = StairwellTop(tag.read("C"))
    }

    override fun StructBuildScopeWithConfig.build() {
        inRelativeView {
            "stairwell_top" putA Point(0,0,0)
        }
    }
}