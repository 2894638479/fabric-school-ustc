package org.schoolustc.structurePieces

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.IRON_BARS
import net.minecraft.world.level.block.Blocks.STONE_BRICKS
import org.schoolustc.structureDsl.*

class OuterWallPiece(
    config:StructGenConfig,
    val length:Int
):MyStruct(Companion,config,Area(0..<length,0..3,0..0)) {
    companion object : MyStructInfo<OuterWallPiece>("wall"){
        override fun loadTag(tag: CompoundTag) = OuterWallPiece(
            tag.getConfig(),
            tag.getInt("l")
        )
        override fun OuterWallPiece.saveTag(tag: CompoundTag) {
            tag.putConfig(config)
            tag.putInt("",length)
        }
        override fun StructBuilder.build(struct: OuterWallPiece) {
            STONE_BRICKS fillS Area(0..<struct.length,0..1,0..0)
            IRON_BARS fillSC Area(0..<struct.length,2..3,0..0)
        }
    }
}