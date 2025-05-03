package org.schoolustc.structs

import net.minecraft.data.worldgen.features.TreeFeatures
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.Area
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.struct.MyStructFixedWidth
import org.schoolustc.structureDsl.struct.MyStructFixedWidthInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class CherrySide(config: StructGenConfig,length:Int): MyStructFixedWidth(Companion,config,length) {
    companion object : MyStructFixedWidthInfo<CherrySide>("cherry_side",3){
        override val constructor = ::CherrySide
        override val defaultDirection = Direction2D.ZPlus
    }

    override fun StructBuildScopeWithConfig.build() {
        inSurfView {
            SMOOTH_STONE_SLAB fill Area(0..2,1..1,0..<length)
            for(z in 2..<length - 3 step 8){
                DIRT fill Point(1,1,z)
                val res = TreeFeatures.CHERRY_BEES_005 plant Point(1,2,z)
                if(res == false) {
                    CHERRY_SAPLING fill Point(1,2,z)
                }
            }
        }
    }
}