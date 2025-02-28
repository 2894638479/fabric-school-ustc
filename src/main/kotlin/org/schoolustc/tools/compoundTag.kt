package org.schoolustc.tools

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag

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

