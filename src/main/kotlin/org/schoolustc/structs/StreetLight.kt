package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import net.minecraft.world.level.block.state.properties.WallSide
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.StructBuildScope
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structureDsl.structure.MyStructFixedSizeInfo

class StreetLight(config: StructGenConfig):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<StreetLight>("streetlight", Point(2,5,1)){
        override fun loadTag(tag: CompoundTag) = StreetLight(tag.getConfig())
        override fun StreetLight.saveTag(tag: CompoundTag) = tag.putConfig(config)
        override val defaultDirection = Direction2D.XPlus
    }
    override fun StructBuildScope.build() {
        val wall = rand from mapOf(
            ACACIA_FENCE to 1f,
            OAK_FENCE to 5f,
            BIRCH_FENCE to 1f
        )
        val light = rand from mapOf(
            SEA_LANTERN to 5f,
            GLOWSTONE to 1f
        )
        wall fill Area(0..0,0..3,0..0)
        wall.state.connected(Direction2D.XPlus) fill Point(0,4,0)
        wall.state.connected(Direction2D.XMin) fill Point(1,4,0)
        light.state fill Point(1,3,0)
    }
}