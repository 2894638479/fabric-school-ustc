package org.schoolustc.structurePieces

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.StructBuilder
import org.schoolustc.structureDsl.struct.StructGenConfig

class GatePiece(config:StructGenConfig):MyStruct(Companion,config) {
    companion object : MyStructInfo<MyStruct>("gate",Area(0..12,0..7,0..3)){
        override fun loadTag(tag: CompoundTag) = GatePiece(tag.getConfig())
        override fun MyStruct.saveTag(tag: CompoundTag) = tag.putConfig(config)
    }
    constructor(area: Area2D,y:Int):this(
        StructGenConfig(
            Point(area.x1 - 1,y,area.z1 - 1),false,false,area.h > area.w
        )
    )
    override fun StructBuilder.build() {
        CALCITE fill Area(1..2,0..7,1..2)
        CALCITE fill Area(10..11,0..7,1..2)
        CALCITE fill Area(3..9,6..7,1..2)
    }
}