package org.schoolustc.structurePieces

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*

class StreetPiece(
    config:StructGenConfig,
    val length : Int
):MyStruct(Companion,config, Area(-3..3,0..0,0..<length)) {
    init { if(length <= 0) error("street length <= 0") }
    companion object : MyStructInfo<StreetPiece>("street") {
        override fun loadTag(tag: CompoundTag) = StreetPiece(
            tag.getConfig(),
            tag.getInt("l")
        )
        override fun StreetPiece.saveTag(tag: CompoundTag) {
            tag.putConfig(config)
            tag.putInt("l",length)
        }
        override fun StructBuilder.build(struct: StreetPiece) {
            GRAY_CONCRETE fillS Area(-3..3,0..0,0..<struct.length)
            WHITE_CONCRETE fillS AreaProg(0..0,0..0,0..<struct.length step 5)
            WHITE_CONCRETE fillS AreaProg(0..0,0..0,1..<struct.length step 5)
            WHITE_CONCRETE fillS AreaProg(0..0,0..0,2..<struct.length step 5)
        }
    }
}