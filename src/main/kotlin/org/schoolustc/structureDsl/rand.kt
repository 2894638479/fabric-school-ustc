package org.schoolustc.structureDsl

import net.minecraft.util.RandomSource

fun RandomSource.nextInt(range:IntProgression):Int{
    val count = range.count()
    val r = nextFloat() * count
    var cur = 0
    range.forEach {
        cur++
        if(cur > r) return it
    }
    return range.last()
}

fun RandomSource.nextBool(trueChance:Float) = nextFloat() < trueChance