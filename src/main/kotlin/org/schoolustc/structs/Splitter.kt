package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.*

class Splitter(
    config: StructGenConfig,
    val length : Int
): MyStruct(Companion,config, Point(1,1,length)) {
    init { if(length <= 0) error("$id length <= 0") }
    companion object : MyStructInfo<Splitter>("splitter") ,IsRoad{
        override val defaultDirection = Direction2D.Z1
        override fun loadTag(tag: CompoundTag) = Splitter(
            tag.getConfig(),
            tag.getInt("l")
        )
        override fun Splitter.saveTag(tag: CompoundTag) {
            tag.putConfig(config)
            tag.putInt("l",length)
        }
        override val period get() = 10
        override val width get() = 1
    }
    override fun StructBuildScope.build() {
        SMOOTH_STONE_SLAB fillS Area(0..0,1..1,0..<length)
    }
}