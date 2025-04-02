package org.schoolustc.structureDsl

import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import org.schoolustc.structureDsl.struct.StructGenConfig


/*
used key:
GD
BB
O
id
 */

fun CompoundTag.putConfig(config: StructGenConfig){
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

fun <T> CompoundTag.putResourceKey(key:ResourceKey<T>) {
    putString("Kr", key.registry().toString())
    putString("Kl", key.location().toString())
}

fun <T> CompoundTag.getResourceKey(): ResourceKey<T> {
    val kr = getString("Kr")
    val kl = getString("Kl")
    val registryId = ResourceLocation.tryParse(kr) ?: error("unknown key registry:$kr")
    val featureId = ResourceLocation.tryParse(kl) ?: error("unknown key location:$kl")
    val registryKey = ResourceKey.createRegistryKey<T>(registryId)
    return ResourceKey.create(registryKey, featureId)
}

