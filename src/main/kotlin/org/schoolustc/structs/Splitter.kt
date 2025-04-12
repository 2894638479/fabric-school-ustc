package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.SMOOTH_STONE
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyRoadStruct
import org.schoolustc.structureDsl.struct.MyRoadStructInfo
import org.schoolustc.structureDsl.struct.StructBuildScope
import org.schoolustc.structureDsl.struct.StructGenConfig

class Splitter(
    config: StructGenConfig,
    val length : Int
): MyRoadStruct(Companion,config, Point(1,1,length)) {
    companion object : MyRoadStructInfo<Splitter>("splitter"){
        override val defaultDirection = Direction2D.ZPlus
        override fun loadTag(tag: CompoundTag) = Splitter(
            tag.read("C"),
            tag.read("l")
        )
        override fun Splitter.saveTag(tag: CompoundTag) {
            tag.write("C",config)
            tag.write("l",length)
        }
        override val period get() = 10
        override val width get() = 1
        override val constructor get() = ::Splitter
    }
    override fun StructBuildScope.build() = inSurfView {
        SMOOTH_STONE fill Area(0..0,0..0,0..<length)
    }
}