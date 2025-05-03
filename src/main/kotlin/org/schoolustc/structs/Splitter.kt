package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.SMOOTH_STONE
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedWidth
import org.schoolustc.structureDsl.struct.MyStructFixedWidthInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class Splitter(
    config: StructGenConfig,
    length : Int
): MyStructFixedWidth(Companion,config, length) {
    companion object : MyStructFixedWidthInfo<Splitter>("splitter",1){
        override val defaultDirection = Direction2D.ZPlus
        override val constructor get() = ::Splitter
    }
    override fun StructBuildScopeWithConfig.build() = inSurfView {
        SMOOTH_STONE fill Area(0..0,0..0,0..<length)
    }
}