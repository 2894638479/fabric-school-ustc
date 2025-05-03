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

class TreeSide(config: StructGenConfig, length:Int): MyStructFixedWidth(Companion,config,length) {
    companion object : MyStructFixedWidthInfo<TreeSide>("tree_side",3){
        override val constructor = ::TreeSide
        override val defaultDirection = Direction2D.ZPlus
        override val profileTimeOutMs = 10L
    }

    override fun StructBuildScopeWithConfig.build() {
        inSurfView {
            SMOOTH_STONE_SLAB fill Area(0..2,1..1,0..<length)
            for(z in 2..<length - 3 step 8){
                DIRT fill Point(1,1,z)
                val treePos = Point(1,2,z)
                val res = TreeFeatures.FANCY_OAK_BEES_005 plant treePos
                if(res == false) {
                    val res2 = TreeFeatures.OAK_BEES_005 plant treePos
                    if(res2 == false) {
                        OAK_SAPLING fill treePos
                    }
                }
            }
        }
    }
}