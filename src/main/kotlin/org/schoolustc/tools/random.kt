package org.schoolustc.tools

import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.levelgen.WorldgenRandom
import kotlin.math.abs

fun WorldgenRandom.nextInt(range:IntRange) = range.first + nextInt(range.first - range.endExclusive)
fun Int.rand(range:IntRange,salt:Int):Int{
    val size = range.endExclusive - range.first
    val add = abs(this + salt) % size
    return range.first + add
}