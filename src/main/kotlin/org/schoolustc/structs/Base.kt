package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScope

class Base(val area: Area2D,val maxY:Int,val block:Block):MyStruct(Companion,area.toArea(0..maxY)) {
    companion object : MyStructInfo<Base>("base"){
        override val defaultDirection = Direction2D.XPlus
        override fun Base.saveTag(tag: CompoundTag) {
            tag.write("a",area)
            tag.write("y",maxY)
            tag.write("b",block)
        }
        override fun loadTag(tag:CompoundTag) = Base(
            tag.read("a"),
            tag.read("y"),
            tag.read("b")
        )
    }

    override fun StructBuildScope.build() {
        inRawView {
            block fillUnder area.toArea((maxY+1).range)
        }
    }
}