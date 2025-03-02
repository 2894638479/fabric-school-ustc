package org.schoolustc.structureDsl

import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents.Register
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import java.rmi.registry.Registry

/*
used key:
GD
BB
O
id
 */

fun CompoundTag.putConfig(config:StructGenConfig){
    putInt("X",config.pos.x)
    putInt("Y",config.pos.y)
    putInt("Z",config.pos.z)
    putBoolean("rX",config.revX)
    putBoolean("rZ",config.revZ)
    putBoolean("rr",config.rotate)
}

fun CompoundTag.getConfig() = StructGenConfig(
    Point(
        getInt("X"),
        getInt("Y"),
        getInt("Z")
    ),
    getBoolean("rX"),
    getBoolean("rZ"),
    getBoolean("rr")
)

fun CompoundTag.putArea(area:Area){
    putIntArray("A", intArrayOf(
        area.x.first,
        area.x.last,
        area.y.first,
        area.y.last,
        area.z.first,
        area.z.last,
    ))
}
fun CompoundTag.getArea():Area{
    val arr = getIntArray("A")
    if(arr.size != 6) error("area read from compound error")
    return Area(
        arr[0]..arr[1],
        arr[2]..arr[3],
        arr[4]..arr[5]
    )
}

fun CompoundTag.putAreaProg(area:AreaProg){
    putIntArray("P", intArrayOf(
        area.x.first,
        area.x.last,
        area.x.step,
        area.y.first,
        area.y.last,
        area.y.step,
        area.z.first,
        area.z.last,
        area.z.step,
    ))
}
fun CompoundTag.getAreaProg():AreaProg{
    val arr = getIntArray("P")
    if(arr.size != 9) error("areaprog read from compound error")
    return AreaProg(
        arr[0]..arr[1] step arr[2],
        arr[3]..arr[4] step arr[5],
        arr[6]..arr[7] step arr[8]
    )
}

fun CompoundTag.putBlock(block: Block){
    putString("BL",BuiltInRegistries.BLOCK.getKey(block).toString())
}
fun CompoundTag.getBlock() = BuiltInRegistries.BLOCK.get(ResourceLocation(getString("BL")))

