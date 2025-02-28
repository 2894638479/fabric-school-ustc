package org.schoolustc.structureDsl

import net.minecraft.world.level.block.state.BlockState

fun interface BlockSelector{
    fun select(): BlockState
}