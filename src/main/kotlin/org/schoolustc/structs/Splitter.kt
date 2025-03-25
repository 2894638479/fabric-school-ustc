package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.*

class Splitter(
    config: StructGenConfig,
    val length : Int
): MyRoadStruct(Companion,config, Point(1,1,length)) {
    companion object : MyRoadStructInfo<Splitter>("splitter"){
        override val defaultDirection = Direction2D.ZPlus
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
        override val constructor get() = ::Splitter
    }
    override fun StructBuildScope.build() {
        SMOOTH_STONE_SLAB fillS Area(0..0,1..1,0..<length)
    }
}