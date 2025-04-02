package org.schoolustc.structureDsl

val IntProgression.length get() = last - first + 1
infix fun IntRange.overlap(other:IntRange) = last in other || first in other || other.first in this || other.last in this
infix fun IntRange.contains(i:Int) = i in this
infix fun IntRange.contains(other:IntRange) = other.first in this && other.last in this
infix fun IntRange.nextTo(other:IntRange) = last + 1 == other.first || first - 1 == other.last
fun IntProgression.toRange() = first..last
fun IntRange.first(count:Int) = first..<first + count
fun IntRange.last(count:Int) = last + 1 - count..last
fun IntRange.expand(count:Int) = first - count..last + count
fun IntRange.expand(start:Int,end:Int) = first - start..last + end
inline val IntRange.middle get() = (first + last) / 2

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