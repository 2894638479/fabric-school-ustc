package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.*
import org.schoolustc.structureDsl.structure.MyStructFixedAreaInfo

class OuterWallCorner(config: StructGenConfig): MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedAreaInfo<OuterWallCorner>("wallcorner",Point(1,5,1)) {
        override val defaultDirection = Direction2D.X1
        override fun loadTag(tag: CompoundTag) = OuterWallCorner(tag.getConfig())
        override fun OuterWallCorner.saveTag(tag: CompoundTag) = tag.putConfig(config)
    }
    override fun StructBuildScope.build() {
        STONE_BRICKS fillS Area(0..0,0..3,0..0)
        val lantern = if(randBool(0.9f)) LANTERN else SOUL_LANTERN
        lantern fillS Point(0,4,0)
    }
}