package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.*

class Street(
    config: StructGenConfig,
    val length : Int
): MyStruct(Companion,config,Point(7,1,length)) {
    init { if(length <= 0) error("$id length <= 0") }
    companion object : MyStructInfo<Street>("street") ,IsRoad{
        override val defaultDirection = Direction2D.Z1
        override fun loadTag(tag: CompoundTag) = Street(
            tag.getConfig(),
            tag.getInt("l")
        )
        override fun Street.saveTag(tag: CompoundTag) {
            tag.putConfig(config)
            tag.putInt("l",length)
        }
        override val width get() = 7
        override val period get() = 10
    }
    override fun StructBuildScope.build() {
        GRAY_CONCRETE fillS Area(0..6,0..0,0..<length)
        WHITE_CONCRETE fillS AreaProg(3..3,0..0,0..<length step 5)
        WHITE_CONCRETE fillS AreaProg(3..3,0..0,1..<length step 5)
        WHITE_CONCRETE fillS AreaProg(3..3,0..0,2..<length step 5)
    }
}