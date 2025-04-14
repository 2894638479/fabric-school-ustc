package org.schoolustc.structureDsl.struct

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType
import org.schoolustc.fullId
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structs

abstract class MyStructInfo <T: MyStruct>(
    val id:String
){
    abstract fun loadTag(tag: CompoundTag):T
    abstract fun T.saveTag(tag: CompoundTag)
    val type = StructurePieceType { _, tag -> loadTag(tag) }
    abstract val defaultDirection:Direction2D
    private val MyStruct.asT get() = this as T
    fun saveAsT(s: MyStruct, tag : CompoundTag) = s.asT.saveTag(tag)
    fun register() {
        Registry.register(BuiltInRegistries.STRUCTURE_PIECE, fullId(id), type)
    }
    open val profileName get() = "struct $id"
    open val profileTimeOutMs get() = 500L
}