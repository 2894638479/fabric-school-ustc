package org.schoolustc.structureDsl.struct

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType
import org.schoolustc.fullId
import org.schoolustc.structureDsl.Area

abstract class MyStructInfo <T: MyStruct>(
    val id:String,
    val area: Area? = null
){
    abstract fun loadTag(tag: CompoundTag):T
    abstract fun T.saveTag(tag: CompoundTag)
    val type = StructurePieceType { _, tag -> loadTag(tag) }
    private val MyStruct.asT get() = this as T
    fun saveAsT(s: MyStruct, tag : CompoundTag) = s.asT.saveTag(tag)
    fun register() {
        Registry.register(BuiltInRegistries.STRUCTURE_PIECE, fullId(id), type)
    }
}