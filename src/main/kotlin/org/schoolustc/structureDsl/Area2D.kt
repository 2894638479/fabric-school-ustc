package org.schoolustc.structureDsl

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
    inline val x1 get() = x.first
    inline val x2 get() = x.last
    inline val z1 get() = z.first
    inline val z2 get() = z.last
    inline val size get() = xl * zl

    fun l(direction: Direction2D) = if(direction.isX) x else z
    fun w(direction: Direction2D) = if(direction.isX) z else x
    fun width(direction: Direction2D) = w(direction).length
    fun length(direction: Direction2D) = l(direction).length
    fun sliceStart(direction:Direction2D,length:Int) = direction.run {
        if(is1) area2D(l.first(length),w)
        else area2D(l.last(length),w)
    }
    fun sliceEnd(direction: Direction2D,length: Int) = sliceStart(direction.reverse,length)
    fun expand(count:Int) = Area2D(x.expand(count),z.expand(count))
    fun expand(direction:Direction2D,count:Int) = direction.run {
        if(is1) area2D(l.expand(0,count),w)
        else area2D(l.expand(count,0),w)
    }
    infix fun overlap(other:Area2D) = x overlap other.x && z overlap other.z
    infix fun contains(other: Area2D) = x contains other.x && z contains other.z
    //分割长条形区间，最终长度在minLen..<2*minLen
    fun split(minLen:Int):List<Area2D>{
        val list = mutableListOf<Area2D>()
        val rotate = xl > zl
        var remain = this
        fun left():Int {
            return if (rotate) remain.xl else remain.zl
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
    fun isEmpty() = x.isEmpty() || z.isEmpty()
    fun ifEmpty(block:()->Unit)=apply{if(isEmpty())block()}
    override fun toString(): String {
        return "x:$x1..$x2  z:$z1..$z2"
    }
    fun checkNotEmpty() = ifEmpty { error("empty area2D: $this") }
}