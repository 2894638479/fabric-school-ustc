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
fun RandomSource.nextBool(trueChance:Double) = nextFloat() < trueChance

infix fun <T> RandomSource.from(collection: Collection<T>):T{
    if(collection.isEmpty()) error("empty collection")
    val index = nextInt(collection.indices)
    collection.forEachIndexed { i, it -> if(index == i) return it }
    error("random error")
}
infix fun RandomSource.from(range:IntProgression) = nextInt(range)
infix fun RandomSource.from(range:ClosedFloatingPointRange<Double>):Double {
    val start = range.start
    val end = range.endInclusive
    return nextDouble() * (end - start) + start
}
infix fun RandomSource.from(range:ClosedFloatingPointRange<Float>):Float{
    val start = range.start
    val end = range.endInclusive
    return nextFloat() * (end - start) + start
}
infix fun <T,V:Number> RandomSource.from(map:Map<T,V>):T{
    val sum = map.values.sumOf { it.toDouble() }
    val r = nextFloat() * sum
    var f = 0.0
    for((block,weight) in map){
        f += weight.toDouble()
        if(r < f) return block
    }
    return map.keys.last()
}
fun <T,V:Number> RandomSource.from(vararg pairs:Pair<T,V>) = from(pairs.toMap())
infix fun RandomSource.from(area:Area2D) = from(area.x) to from(area.z)