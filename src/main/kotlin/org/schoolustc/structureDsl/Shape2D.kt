package org.schoolustc.structureDsl

import java.util.*

class Shape2D(area2D: Area2D? = null) : Sequence<Point> {
    private val map = TreeMap<Int,MutableList<IntRange>>()
    private fun row(x:Int) = map[x] ?: mutableListOf<IntRange>().apply { map[x] = this }
    init{
        area2D?.run {
            for (xx in x){
                map[xx] = mutableListOf(z)
            }
        }
    }
    fun addPoint(x:Int,z:Int){
        val row = row(x)
        for (i in row.indices) {
            row[i] = row[i].add(z) ?: continue
            return
        }
        row += z..z
    }
    fun delPoint(x:Int,z:Int){
        val row = row(x)
        for(i in row.indices) {
            val range = row[i]
            if(z in range) range.run {
                if(first == last) {
                    row.removeAt(i)
                } else if(first == z) {
                    row[i] = first + 1..last
                } else if(last == z) {
                    row[i] = first..last - 1
                } else {
                    row[i] = first..<z
                    row += z+1..last
                }
                return
            }
        }
    }
    var y = 0
    private val seq = sequence {
        map.forEach { (t, u) -> u.forEach { it.forEach { yield(Point(t,y,it)) } } }
    }
    override fun iterator() = seq.iterator()
    operator fun contains(point: Point):Boolean{
        map[point.x]?.forEach { if(point.y in it) return true }
        return false
    }
    infix fun overlaps(other:Shape2D):Boolean{
        val xIterator = map.navigableKeySet().iterator()
        val otherXIterator = other.map.navigableKeySet().iterator()
        var currentX: Int? = xIterator.next()
        var otherCurrentX: Int? = otherXIterator.next()
        fun Iterator<Int>.nextOrNull() = if(hasNext()) next() else null
        while (currentX != null && otherCurrentX != null) {
            when {
                currentX == otherCurrentX -> {
                    if (checkRowOverlap(currentX, other.map[otherCurrentX]!!)) {
                        return true
                    }
                    currentX = xIterator.nextOrNull()
                    otherCurrentX = otherXIterator.nextOrNull()
                }
                currentX < otherCurrentX -> currentX = xIterator.nextOrNull()
                else -> otherCurrentX = otherXIterator.nextOrNull()
            }
        }
        return false
    }
    private fun checkRowOverlap(x: Int, otherRanges: List<IntRange>): Boolean {
        val thisRanges = map[x] ?: return false
        var i = 0
        var j = 0
        while (i < thisRanges.size && j < otherRanges.size) {
            val a = thisRanges[i]
            val b = otherRanges[j]
            when {
                a.last < b.first -> i++
                b.last < a.first -> j++
                else -> return true
            }
        }
        return false
    }
}