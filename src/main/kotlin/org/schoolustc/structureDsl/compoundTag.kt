package org.schoolustc.structureDsl

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import org.schoolustc.structureDsl.struct.StructGenConfig


/*
used key:
GD
BB
O
id
 */

fun CompoundTag.putConfig(config: StructGenConfig,key:String = "C"){
    putIntArray(key,config.toIntArray())
}

fun CompoundTag.getConfig(key:String = "C") = StructGenConfig.fromIntArray(getIntArray(key))

fun <T> CompoundTag.putResourceKey(k:ResourceKey<T>,key:String = "K") {
    putString(key,"${k.registry()}\n${k.location()}")
}

fun <T> CompoundTag.getResourceKey(key:String = "K"): ResourceKey<T> {
    val k = getString(key)
    val kr = k.substringBefore('\n')
    val kl = k.substringAfter('\n')
    val registryId = ResourceLocation.tryParse(kr) ?: error("unknown key registry:$kr")
    val featureId = ResourceLocation.tryParse(kl) ?: error("unknown key location:$kl")
    val registryKey = ResourceKey.createRegistryKey<T>(registryId)
    return ResourceKey.create(registryKey, featureId)
}
fun CompoundTag.putBlock(block:Block,key:String = "b") {
    putString(key,block.descriptionId)
}
fun CompoundTag.getBlock(key:String = "b"):Block{
    val str = getString(key)
    val location = ResourceLocation.tryParse(str) ?: error("unknown block id $str")
    return BuiltInRegistries.BLOCK.get(location)
}
fun CompoundTag.putArea2D(area: Area2D,key:String = "a"){
    putIntArray(key, intArrayOf(area.x1,area.x2,area.z1,area.z2))
}
fun CompoundTag.getArea2D(key:String = "a"):Area2D{
    val arr = getIntArray(key)
    arr.size.match(4)
    return Area2D(arr[0]..arr[1],arr[2]..arr[3])
}
fun CompoundTag.putDirection2D(direction:Direction2D,key:String = "d") = putInt(key,direction.toInt())
fun CompoundTag.getDirection2D(key:String = "d") = Direction2D.fromInt(getInt(key))

