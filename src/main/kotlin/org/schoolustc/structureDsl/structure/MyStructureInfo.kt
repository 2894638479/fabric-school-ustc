package org.schoolustc.structureDsl.structure

import com.mojang.datafixers.Products
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings
import net.minecraft.world.level.levelgen.structure.Structure.settingsCodec
import net.minecraft.world.level.levelgen.structure.StructureType
import org.schoolustc.fullId

abstract class MyStructureInfo<T:MyStructure> (
    val id:String, private val constructor:(StructureSettings) -> T,
    private val codecBuilder
    : (RecordCodecBuilder.Instance<T>) -> Products.P1<RecordCodecBuilder.Mu<T>,StructureSettings>
    = { it.group(settingsCodec(it)) }
) {
    private val CODEC = RecordCodecBuilder.create { codecBuilder(it).apply(it,constructor) }
    val type = StructureType { CODEC }
    fun register(){
        Registry.register(BuiltInRegistries.STRUCTURE_TYPE,fullId(id),type)
    }
}