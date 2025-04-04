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
inline fun <reified T : Any> CompoundTag.write(key:String,t:T,) = when(T::class){
    Int::class -> putInt(key,t as Int)
    Byte::class -> putByte(key,t as Byte)
    Short::class -> putShort(key,t as Short)
    Long::class -> putLong(key,t as Long)
    Float::class -> putFloat(key,t as Float)
    Double::class -> putDouble(key,t as Double)
    Boolean::class -> putBoolean(key,t as Boolean)
    StructGenConfig::class -> putConfig(key,t as StructGenConfig)
    Block::class -> putBlock(key,t as Block)
    Area2D::class -> putArea2D(key,t as Area2D)
    Direction2D::class -> putDirection2D(key,t as Direction2D)
    else -> error("not supported type: ${T::class}")
}


inline fun <reified T : Any> CompoundTag.read(key:String):T = when(T::class){
    Int::class -> getInt(key) as T
    Byte::class -> getByte(key) as T
    Short::class -> getShort(key) as T
    Long::class -> getLong(key) as T
    Float::class -> getFloat(key) as T
    Double::class -> getDouble(key) as T
    Boolean::class -> getBoolean(key) as T
    StructGenConfig::class -> getConfig(key) as T
    Block::class -> getBlock(key) as T
    Area2D::class -> getArea2D(key) as T
    Direction2D::class -> getDirection2D(key) as T
    else -> error("not supported type: ${T::class}")
}


fun CompoundTag.putConfig(key:String,config: StructGenConfig){
    putIntArray(key,config.toIntArray())
    val a:Int = read("")
}

fun CompoundTag.getConfig(key:String) = StructGenConfig.fromIntArray(getIntArray(key))

fun <T> CompoundTag.putResourceKey(key:String,k:ResourceKey<T>) {
    putString(key,"${k.registry()}\n${k.location()}")
}

fun <T> CompoundTag.getResourceKey(key:String): ResourceKey<T> {
    val k = getString(key)
    val kr = k.substringBefore('\n')
    val kl = k.substringAfter('\n')
    val registryId = ResourceLocation.tryParse(kr) ?: error("unknown key registry:$kr")
    val featureId = ResourceLocation.tryParse(kl) ?: error("unknown key location:$kl")
    val registryKey = ResourceKey.createRegistryKey<T>(registryId)
    return ResourceKey.create(registryKey, featureId)
}
fun CompoundTag.putBlock(key:String,block:Block) {
    putString(key,block.descriptionId)
}
fun CompoundTag.getBlock(key:String):Block{
    val str = getString(key)
    val location = ResourceLocation.tryParse(str) ?: error("unknown block id $str")
    return BuiltInRegistries.BLOCK.get(location)
}
fun CompoundTag.putArea2D(key:String,area: Area2D){
    putIntArray(key, intArrayOf(area.x1,area.x2,area.z1,area.z2))
}
fun CompoundTag.getArea2D(key:String):Area2D{
    val arr = getIntArray(key)
    arr.size.match(4)
    return Area2D(arr[0]..arr[1],arr[2]..arr[3])
}
fun CompoundTag.putDirection2D(key:String,direction:Direction2D) = putInt(key,direction.toInt())
fun CompoundTag.getDirection2D(key:String) = Direction2D.fromInt(getInt(key))

