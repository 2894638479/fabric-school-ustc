package org.schoolustc.structurePieces

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*

class OuterWallCornerPiece(config:StructGenConfig):MyStruct(Companion,config,Area(0..0,0..4,0..0)) {
    companion object : MyStructInfo<OuterWallCornerPiece>("wallcorner") {
        override fun loadTag(tag: CompoundTag) = OuterWallCornerPiece(tag.getConfig())
        override fun OuterWallCornerPiece.saveTag(tag: CompoundTag) = tag.putConfig(config)
        override fun StructBuilder.build(struct: OuterWallCornerPiece) {
            STONE_BRICKS fillS Area(0..0,0..3,0..0)
            val lantern = if(randBool(0.9f)) LANTERN else SOUL_LANTERN
            lantern fillS Point(0,4,0)
        }
    }
}