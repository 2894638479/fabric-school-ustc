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