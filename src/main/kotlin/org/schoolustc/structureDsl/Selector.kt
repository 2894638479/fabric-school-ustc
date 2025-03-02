package org.schoolustc.structureDsl

import net.minecraft.world.level.block.state.BlockState

fun interface Selector <T> {
    fun select(): T
}