package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.*
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class Road(
    config: StructGenConfig,
    length : Int
): MyStructFixedWidth(Companion,config, length) {
    companion object : MyStructFixedWidthInfo<Road>("road",3){
        override val defaultDirection = Direction2D.ZPlus
        override val constructor get() = ::Road
    }
    override fun StructBuildScopeWithConfig.build() = inSurfView {
        DIRT_PATH fill Area(0..2,0..0,0..<length)
    }
}