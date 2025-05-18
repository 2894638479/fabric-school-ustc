package org.schoolustc.structureDsl

import kotlinx.serialization.json.Json
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import org.schoolustc.calc.Pt
import org.schoolustc.items.StudentCardItem
import org.schoolustc.logger
import org.schoolustc.structs.Tree
import org.schoolustc.structureDsl.struct.scope.StructGenConfig
import java.time.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/*
used key:
GD
BB
O
id
 */
inline fun <reified T : Any> CompoundTag.write(key:String,t:T) = when(T::class){
    Int::class -> putInt(key,t as Int)
    Byte::class -> putByte(key,t as Byte)
    Short::class -> putShort(key,t as Short)
    Long::class -> putLong(key,t as Long)
    Float::class -> putFloat(key,t as Float)
    Double::class -> putDouble(key,t as Double)
    Boolean::class -> putBoolean(key,t as Boolean)
    String::class -> putString(key,t as String)
    StructGenConfig::class -> putConfig(key,t as StructGenConfig)
    Block::class -> putBlock(key,t as Block)
    Area2D::class -> putArea2D(key,t as Area2D)
    Orientation2D::class -> putOrientation2D(key,t as Orientation2D)
    Tree.TreeType::class -> putTreeType(key,t as Tree.TreeType)
    Pt::class -> putPt(key,t as Pt)
    Point::class -> putPoint(key,t as Point)
    ZonedDateTime::class -> putZonedDateTime(key,t as ZonedDateTime)
    StudentCardItem.SubjectInfo::class -> putSerializable(key,t as StudentCardItem.SubjectInfo)
    else -> if(T::class.java.isEnum) putEnum(key,t as Enum<*>)
        else error("not supported type: ${T::class}")
}


inline fun <reified T : Any> CompoundTag.read(key:String):T = when(T::class){
    Int::class -> getInt(key) as T
    Byte::class -> getByte(key) as T
    Short::class -> getShort(key) as T
    Long::class -> getLong(key) as T
    Float::class -> getFloat(key) as T
    Double::class -> getDouble(key) as T
    Boolean::class -> getBoolean(key) as T
    String::class -> getString(key) as T
    StructGenConfig::class -> getConfig(key) as T
    Block::class -> getBlock(key) as T
    Area2D::class -> getArea2D(key) as T
    Orientation2D::class -> getOrientation2D(key) as T
    Tree.TreeType::class -> getTreeType(key) as T
    Pt::class -> getPt(key) as T
    Point::class -> getPoint(key) as T
    ZonedDateTime::class -> getZonedDateTime(key) as T
    StudentCardItem.SubjectInfo::class -> getSerializable(key) ?: StudentCardItem.SubjectInfo(listOf()) as T
    else -> if(T::class.java.isEnum) getEnum<T>(key)
        else error("not supported type: ${T::class}")
}

inline fun <reified T:Any> tagMember(name:String) = object : ReadWriteProperty<CompoundTag,T> {
    override operator fun getValue(thisRef: CompoundTag, property: KProperty<*>):T = thisRef.read<T>(name)
    override operator fun setValue(thisRef: CompoundTag, property: KProperty<*>, value:T) = thisRef.write<T>(name,value)
}
inline fun <reified T:Any> itemMember(name:String) = object : ReadWriteProperty<ItemStack,T> {
    override operator fun getValue(thisRef: ItemStack, property: KProperty<*>):T = thisRef.orCreateTag.read<T>(name)
    override operator fun setValue(thisRef: ItemStack, property: KProperty<*>, value:T) = thisRef.orCreateTag.write<T>(name,value)
}

fun CompoundTag.putConfig(key:String,config: StructGenConfig) = putIntArray(key,config.toIntArray())
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
    putString(key,BuiltInRegistries.BLOCK.getKey(block).toString())
}
fun CompoundTag.getBlock(key:String):Block{
    val str = getString(key)
    val location = ResourceLocation.tryParse(str) ?: error("unknown block id $str")
    val block = BuiltInRegistries.BLOCK.get(location)
    return block
}
fun CompoundTag.putArea2D(key:String,area: Area2D){
    putIntArray(key, intArrayOf(area.x1,area.x2,area.z1,area.z2))
}
fun CompoundTag.getArea2D(key:String):Area2D{
    val arr = getIntArray(key)
    arr.size.match(4)
    return Area2D(arr[0]..arr[1],arr[2]..arr[3])
}
fun CompoundTag.putOrientation2D(key: String,orientation:Orientation2D) = putDouble(key,orientation.value)
fun CompoundTag.getOrientation2D(key: String) = Orientation2D(getDouble(key))
fun CompoundTag.putTreeType(key:String,type:Tree.TreeType) = putInt(key,type.toInt())
fun CompoundTag.getTreeType(key: String) = Tree.TreeType.fromInt(getInt(key))
fun CompoundTag.putPt(key: String,pt: Pt) = putLongArray(key, longArrayOf(pt.x.toRawBits(),pt.z.toRawBits()))
fun CompoundTag.getPt(key: String) = getLongArray(key).match { it.size == 2 }.let { Pt(Double.fromBits(it[0]),Double.fromBits(it[1])) }
fun CompoundTag.putPoint(key:String,point:Point) = putIntArray(key, intArrayOf(point.x,point.y,point.z))
fun CompoundTag.getPoint(key:String) = getIntArray(key).match { it.size == 3 }.run { Point(get(0),get(1),get(2)) }
fun CompoundTag.putZonedDateTime(key: String,time: ZonedDateTime) = putLong(key,time.toEpochSecond())
fun CompoundTag.getZonedDateTime(key: String) = Instant.ofEpochSecond(getLong(key)).atZone(ZoneId.systemDefault())


fun <T:Enum<*>> CompoundTag.putEnum(key:String,enum:T) = putInt(key,enum.ordinal)
inline fun <reified T> CompoundTag.getEnum(key:String):T {
    val constants = T::class.java.enumConstants ?: error("${T::class} is not enum")
    val index = getInt(key)
    if(index !in constants.indices) {
        logger.warn("enum ${T::class} out of bound: int $index")
        return constants[0]
    }
    return constants[index]
}

inline fun <reified T:Any> CompoundTag.putSerializable(key:String,t:T) = putString(key, Json.encodeToString(t))
inline fun <reified T:Any> CompoundTag.getSerializable(key: String) = try {
    Json.decodeFromString<T>(getString(key))
} catch (_:Exception){
    logger.warn("compound json decode error, json str:${getString(key)}")
    null
}
