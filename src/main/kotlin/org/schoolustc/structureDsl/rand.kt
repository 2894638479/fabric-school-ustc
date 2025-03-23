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

infix fun <T> RandomSource.from(collection: Collection<T>):T{
    if(collection.isEmpty()) error("empty collection")
    val index = nextInt(collection.indices)
    collection.forEachIndexed { i, it -> if(index == i) return it }
    error("random error")
}