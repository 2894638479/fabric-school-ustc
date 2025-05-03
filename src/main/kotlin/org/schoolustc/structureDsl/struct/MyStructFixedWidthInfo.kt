package org.schoolustc.structureDsl.struct

import net.minecraft.nbt.CompoundTag
import org.schoolustc.structureDsl.read
import org.schoolustc.structureDsl.struct.scope.StructGenConfig
import org.schoolustc.structureDsl.write

abstract class MyStructFixedWidthInfo<T:MyStructFixedWidth>(
    id:String,val width:Int) : MyStructInfo<T>(id){
    abstract val constructor:(config: StructGenConfig, length:Int)->T
    override fun T.saveTag(tag: CompoundTag) {
        tag.write("C",config)
        tag.write("l",length)
    }
    override fun loadTag(tag: CompoundTag) = constructor(
        tag.read("C"),
        tag.read("l")
    )
}