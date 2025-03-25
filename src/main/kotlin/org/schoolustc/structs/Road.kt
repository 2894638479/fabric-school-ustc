package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.*

class Road(
    config: StructGenConfig,
    val length : Int
): MyRoadStruct(Companion,config, Point(3,1,length)) {
    companion object : MyRoadStructInfo<Road>("road"){
        override val defaultDirection = Direction2D.Z1
        override fun loadTag(tag: CompoundTag) = Road(
            tag.getConfig(),
            tag.getInt("l")
        )
        override fun Road.saveTag(tag: CompoundTag) {
            tag.putConfig(config)
            tag.putInt("l",length)
        }
        override val width get() = 3
        override val period get() = 10
        override val constructor get() = ::Road
    }
    override fun StructBuildScope.build() {
        DIRT_PATH fillS Area(0..2,0..0,0..<length)
    }
}