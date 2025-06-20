package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class StreetLight(config: StructGenConfig):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<StreetLight>("streetlight", Point(2,5,1)){
        override fun loadTag(tag: CompoundTag) = StreetLight(tag.read("C"))
        override fun StreetLight.saveTag(tag: CompoundTag) = tag.write("C",config)
        override val defaultDirection = Direction2D.XPlus
    }
    override fun StructBuildScopeWithConfig.build() {
        val wall = rand from mapOf(
            ACACIA_FENCE to 1f,
            OAK_FENCE to 5f,
            BIRCH_FENCE to 1f
        )
        val light = rand from mapOf(
            SEA_LANTERN to 5f,
            GLOWSTONE to 1f
        )
        inRelativeView {
            wall fill Area(0..0,0..3,0..0)
            wall.state.connected(Direction2D.XPlus) fill Point(0,4,0)
            wall.state.connected(Direction2D.XMin) fill Point(1,4,0)
            light.state fill Point(1,3,0)
        }
    }
}