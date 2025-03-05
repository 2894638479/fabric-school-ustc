package org.schoolustc.structureDsl

import org.schoolustc.structurePieces.StreetPiece

class Area2D(
    val x:IntRange,
    val z:IntRange
){
    val w get() = x.last - x.first + 1
    val h get() = z.last - z.first + 1
    fun padding(i:Int) = Area2D(
        x.first + 1 .. x.last - 1,
        z.first + 1 .. z.last - 1
    )
    val x1 get() = x.first
    val x2 get() = x.last
    val z1 get() = z.first
    val z2 get() = z.last
    val size get() = w * h

    //分割长条形区间，最终长度在minLen..<2*minLen
    fun split(minLen:Int):List<Area2D>{
        val list = mutableListOf<Area2D>()
        val rotate = w > h
        var remain = this
        fun left():Int {
            return if (rotate) remain.w else remain.h
        }
        fun split():Area2D = remain.run {
            if(rotate){
                val newRemain = Area2D(x1+minLen..x2,z)
                val result = Area2D(x1..<x1 + minLen,z)
                remain = newRemain
                return result
            } else {
                val newRemain = Area2D(x,z1+minLen..z2)
                val result = Area2D(x,z1..<z1+minLen)
                remain = newRemain
                return result
            }
        }
        while(left() >= 2*minLen){
            list.add(split())
        }
        list.add(remain)
        return list
    }
}