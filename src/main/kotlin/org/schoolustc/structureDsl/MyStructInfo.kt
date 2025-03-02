package org.schoolustc.structureDsl

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType
import org.schoolustc.fullId
import org.schoolustc.logger

abstract class MyStructInfo <T:MyStruct>(
    val id:String,
    val area:Area? = null
){
    abstract fun loadTag(tag: CompoundTag):T
    abstract fun T.saveTag(tag: CompoundTag)
    abstract fun StructBuilder.build(struct:T)
    val type = StructurePieceType { _, tag -> loadTag(tag) }
    private val MyStruct.asT get() = this as T
    fun saveAsT(s:MyStruct, tag : CompoundTag) = s.asT.saveTag(tag)
    fun buildAsT(s:MyStruct,builder: StructBuilder) = builder.build(s.asT)
    fun register() {
        Registry.register(BuiltInRegistries.STRUCTURE_PIECE, fullId(id), type)
    }
}