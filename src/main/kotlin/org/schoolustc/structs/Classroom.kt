package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.*
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo

class Classroom(config: StructGenConfig): MyStructFixedSize(Companion,config){
    companion object : MyStructFixedSizeInfo<Classroom>(
        "classroom",
        Point(8,5,12)
    ) {
        override val defaultDirection = Direction2D.XPlus
        override fun loadTag(tag: CompoundTag): Classroom {
            return Classroom(tag.read("C"))
        }
        override fun Classroom.saveTag(tag: CompoundTag) {
            tag.write("C",config)
        }
    }
    override fun StructBuildScope.build() {
    }
}

