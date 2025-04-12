package org.schoolustc.structureDsl

import java.lang.Math.pow
import kotlin.math.*

class Area2D(
    val x:IntRange,
    val z:IntRange
){
    companion object {
        fun Direction2D.area2D(l:IntRange,w:IntRange):Area2D =
            if(isX) Area2D(l,w) else Area2D(w,l)
    }
    constructor(x1:Int,z1:Int,w:Int,h:Int):this(x1..<x1+w,z1..<z1+h)
    inline val xl get() = x.length
    inline val zl get() = z.length
    fun padding(i:Int) = Area2D(
        x.first + i .. x.last - i,
        z.first + i .. z.last - i
    )
    fun padding(i:Int,direction:Direction2D) = slice(direction,0..<length(direction) - i)
    inline val x1 get() = x.first
    inline val x2 get() = x.last
    inline val z1 get() = z.first
    inline val z2 get() = z.last
    inline val size get() = xl * zl

    fun bound(direction:Direction2D) = when(direction){
        Direction2D.XPlus -> x2
        Direction2D.XMin -> x1
        Direction2D.ZPlus -> z2
        Direction2D.ZMin -> z1
    }
    fun l(direction: Direction2D) = if(direction.isX) x else z
    fun w(direction: Direction2D) = if(direction.isX) z else x
    fun width(direction: Direction2D) = w(direction).length
    fun length(direction: Direction2D) = l(direction).length
    fun sliceStart(direction:Direction2D,length:Int) = direction.run {
        if(isPlus) area2D(l.first(length),w)
        else area2D(l.last(length),w)
    }
    fun sliceEnd(direction: Direction2D,length: Int) = sliceStart(direction.reverse,length)
    fun slice(direction:Direction2D,index:IntRange) = direction.run {
        if(isPlus) area2D(l.first + index.first..l.first + index.last,w)
        else area2D(l.last - index.last..l.last - index.first,w)
    }
    fun offset(direction: Direction2D,length: Int) = direction.run {
        fun Int.off() = if(isPlus) this + length else this - length
        area2D(l.first.off()..l.last.off(),w)
    }
    fun expand(count:Int) = Area2D(x.expand(count),z.expand(count))
    fun expand(direction:Direction2D,count:Int) = direction.run {
        if(isPlus) area2D(l.expand(0,count),w)
        else area2D(l.expand(count,0),w)
    }
    infix fun overlap(other:Area2D) = x overlap other.x && z overlap other.z
    infix fun contains(other: Area2D) = x contains other.x && z contains other.z
    infix fun nextTo(other:Area2D):Direction2D?{
        if(z overlap other.z) {
            if(x1 - 1 == other.x2) return Direction2D.XMin
            if(x2 + 1 == other.x1) return Direction2D.XPlus
        } else if(x overlap other.x) {
            if(z1 - 1 == other.z2) return Direction2D.ZMin
            if(z2 + 1 == other.z1) return Direction2D.ZPlus
        }
        return null
    }
    fun isEmpty() = x.isEmpty() || z.isEmpty()
    fun ifEmpty(block:()->Unit)=apply{if(isEmpty())block()}
    override fun toString(): String {
        return "x:$x1..$x2  z:$z1..$z2"
    }
    fun checkNotEmpty() = ifEmpty { error("empty area2D: $this") }
    inline fun iterate(block:(Int,Int)->Unit) {for(x in x){for(z in z){
      block(x,z)
    }}}
    fun middle(y:(Int,Int)->Int):Point{
        val xMid = x.middle.roundToInt()
        val zMid = z.middle.roundToInt()
        return Point(xMid,y(xMid,zMid),zMid)
    }
    fun distanceToMid(other:Area2D) =
        sqrt((x.middle - other.x.middle).pow(2.0) + (z.middle - other.z.middle).pow(2.0))
    fun midY(getY:(Int,Int)->Int):Double{
        var sum = 0
        iterate { x, z -> sum += getY(x,z) }
        return sum / size.toDouble()
    }
    fun width(orientation:Orientation2D):Double{
        val rad = orientation.rad
        val sin = sin(rad).absoluteValue
        val cos = cos(rad).absoluteValue
        return sin * x.length + cos * z.length
    }
    fun length(orientation: Orientation2D) = width(orientation.left(90.0))
    operator fun contains(point:Point) = point.x in x && point.z in z
}