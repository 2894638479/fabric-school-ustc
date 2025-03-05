package org.schoolustc.structurePieces

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.StructBuilder
import org.schoolustc.structureDsl.struct.StructGenConfig

class RoadPiece(
    config: StructGenConfig,
    val length : Int
): MyStruct(Companion,config, Area(-1..1,0..0,0..<length)) {
    init { if(length <= 0) error("$id length <= 0") }
    constructor(area:Area2D,rotate:Boolean):this(
        area.run {
            StructGenConfig(
                Point(if(rotate) x1 else x1 + 1, 0, if(!rotate) z1 else z1 + 1),
                false, false,rotate
            )
        },
        area.run { if(rotate) w else h }
    )
    companion object : MyStructInfo<RoadPiece>("road") {
        override fun loadTag(tag: CompoundTag) = RoadPiece(
            tag.getConfig(),
            tag.getInt("l")
        )
        override fun RoadPiece.saveTag(tag: CompoundTag) {
            tag.putConfig(config)
            tag.putInt("l",length)
        }
    }
    override fun StructBuilder.build() {
        DIRT_PATH fillS Area(-1..1,0..0,0..<length)
    }
}