package org.schoolustc.structureDsl

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType
import org.schoolustc.SchoolUSTC.logger

abstract class MyStructInfo <T:MyStruct>(
    val id:String,
    val area:Area
){
    abstract fun StructBuilder.build()
    abstract fun loadTag(tag: CompoundTag):T
    abstract fun T.saveTag(tag: CompoundTag)
    val type = StructurePieceType { _, tag -> loadTag(tag) }
    fun saveStructTag(s:MyStruct,tag : CompoundTag){
        val struct = s as? T
        struct?.saveTag(tag) ?: logger.error("type convert error:${s.javaClass.name}")
    }
    fun register() {
        Registry.register(BuiltInRegistries.STRUCTURE_PIECE, id, type)
    }
}