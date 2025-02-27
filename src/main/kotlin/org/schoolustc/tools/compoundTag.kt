package org.schoolustc.tools

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag

fun CompoundTag.putPos(pos: BlockPos){
    putInt("X",pos.x)
    putInt("Y",pos.y)
    putInt("Z",pos.z)
}
fun CompoundTag.getPos(): BlockPos = BlockPos(getInt("X"),getInt("Y"),getInt("Z"))
