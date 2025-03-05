package org.schoolustc.structurePieces

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.StructBuilder
import org.schoolustc.structureDsl.struct.StructGenConfig

class SplitterPiece(
    config: StructGenConfig,
    val length : Int
): MyStruct(Companion,config, Area(0..0,0..0,0..<length)) {
    init { if(length <= 0) error("$id length <= 0") }
    constructor(area:Area2D,rotate:Boolean):this(
        area.run {
            StructGenConfig(
                Point(x1, 0, z1),
                false, false,rotate
            )
        },
        area.run { if(rotate) w else h }
    )
    companion object : MyStructInfo<SplitterPiece>("splitter") {
        override fun loadTag(tag: CompoundTag) = SplitterPiece(
            tag.getConfig(),
            tag.getInt("l")
        )
        override fun SplitterPiece.saveTag(tag: CompoundTag) {
            tag.putConfig(config)
            tag.putInt("l",length)
        }
    }
    override fun StructBuilder.build() {
        SMOOTH_STONE_SLAB fillS Area(0..0,1..1,0..<length)
    }
}