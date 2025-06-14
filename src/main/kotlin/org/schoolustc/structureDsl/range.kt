package org.schoolustc.structureDsl

val IntProgression.length get() = last - first + 1
infix fun IntRange.overlap(other:IntRange) = last in other || first in other || other.first in this || other.last in this
infix fun IntRange.contains(i:Int) = i in this
infix fun IntRange.contains(other:IntRange) = other.first in this && other.last in this
infix fun IntRange.nextTo(other:IntRange) = last + 1 == other.first || first - 1 == other.last
infix fun IntRange.nextTo(other:Int) = last + 1 == other || first - 1 == other
fun IntRange.add(i:Int): IntRange?{
    if(last + 1 == i) return first..last+1
    if(first - 1 == i) return first - 1..last
    return null
}
fun IntRange.filter(other:IntRange):List<IntRange>{
    if(other.contains(this)) return listOf()
    if(!overlap(other)) return listOf(this)
    if(contains(other)) return listOf(first..other.first - 1,other.last + 1..last).filter { !it.isEmpty() }
    if(first in other) return listOf(other.last+1..last)
    if(last in other) return listOf(first..other.first-1)
    error("IntRange filter logic error")
}
fun IntProgression.toRange() = first..last
fun IntRange.first(count:Int) = first..<first + count
fun IntRange.last(count:Int) = last + 1 - count..last
fun IntRange.expand(count:Int) = first - count..last + count
fun IntRange.expand(start:Int,end:Int) = first - start..last + end
fun IntRange.padding(count:Int) = expand(-count)
fun IntRange.offset(count: Int) = first+count..last+count
inline val IntRange.middle get() = (first + last) / 2.0
inline val Int.range get() = this..this
val maxRange = Int.MIN_VALUE..Int.MAX_VALUE
fun IntRange.toDouble() = first.toDouble()..last().toDouble()

//较为均匀地分割区间
fun IntRange.split(maxLength:Int):List<Int>{
    val full = last - first
    val count = full / (maxLength + 1) + 1
    val each = full / count
    val rest = full % count
    val length = List(count){ if(it < rest) each + 1 else each }
    val result = mutableListOf<Int>()
    length.forEachIndexed { i, it ->
        result += if(i == 0) first + it
        else result[i-1] + it
    }
    return listOf(first) + result
}