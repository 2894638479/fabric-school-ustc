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
        val block = ACACIA_FENCE
        block fillS Area(0..0,0..3,0..0)
        block.state.connected(Direction2D.XPlus) fillS Point(0,4,0)
        block.state.connected(Direction2D.XMin) setTo Point(0,4,0).finalSurfacePos.offset(Direction2D.XPlus.finalDirection.toDirection())
        SEA_LANTERN.state setTo Point(0,3,0).finalSurfacePos.offset(Direction2D.XPlus.finalDirection.toDirection())
    }
}