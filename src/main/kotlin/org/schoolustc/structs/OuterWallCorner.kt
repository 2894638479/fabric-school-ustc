package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.*
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo

class OuterWallCorner(config: StructGenConfig): MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<OuterWallCorner>("wallcorner",Point(1,5,1)) {
        override val defaultDirection = Direction2D.XPlus
        override fun loadTag(tag: CompoundTag) = OuterWallCorner(tag.read("C"))
        override fun OuterWallCorner.saveTag(tag: CompoundTag) = tag.write("C",config)
    }
    override fun StructBuildScope.build() {
        inSurfView {
            STONE_BRICKS fill Area(0..0, 0..3, 0..0)
            val lantern = if (rand.nextBool(0.9f)) LANTERN else SOUL_LANTERN
            lantern fill Point(0, 4, 0)
        }
    }
}