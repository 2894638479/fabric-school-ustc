package org.schoolustc.calc

import org.schoolustc.structureDsl.Orientation2D
import org.schoolustc.structureDsl.Shape2D
import kotlin.math.roundToInt

class Bezier private constructor(val p1:Pt,val p2:Pt,val c1:Pt,val c2:Pt,val o1:Orientation2D,val o2:Orientation2D) {
    companion object {
        fun byOrientation(p1: Pt,p2: Pt,o1:Orientation2D,o2:Orientation2D):Bezier{
            val d = p1.distanceTo(p2) / 3
            return Bezier(p1,p2,p1.offset(o1,d),p2.offset(o2,d),o1,o2)
        }
        fun byPoint(p1:Pt,p2:Pt,c1:Pt,c2:Pt):Bezier = Bezier(p1,p2,c1,c2,p1.orientationTo(c1),p2.orientationTo(c2))
    }
    val d = p1.distanceTo(p2)
    operator fun invoke(t:Double):Pt{
        val T = 1-t
        val TT = T*T
        val tt = t*t
        fun f(get: Pt.()->Double) = TT*T*p1.get() + 3*TT*t*c1.get() + 3*T*tt*c2.get() + tt*t*p2.get()
        return Pt(f{x},f{z})
    }
    fun orientation(t:Double):Orientation2D{
        val T = 1-t
        fun f(get: Pt.()->Double) = 3*T*T*(c1.get()-p1.get()) + 6*t*T*(c2.get()-c1.get()) + 3*t*t*(p2.get()-c2.get())
        return Pt(0.0,0.0).orientationTo(Pt(f{x},f{z}))
    }
    fun distanceTo(p:Pt):Double{
        val s = 1 / d
        var t = 0.0
        var result = Double.MAX_VALUE
        while(t <= 1){
            p.distanceTo(invoke(t)).let {
                if(it < result) result = it
            }
            t += s
        }
        return result
    }
    fun getNearPoints(w:Double) = getNearPoints(w){ start, end ->
        (!start || atOrientationOf(o1,p1)) && (!end || atOrientationOf(o2,p2))
    }
    fun getNearPoints(w:Double,filter:Pt.(Boolean,Boolean)->Boolean):Shape2D{
        var t = 0.0
        var T = 1.0
        val step = 1.0 / (d)
        val points = Shape2D()
        while(t <= 1.0){
            val b = invoke(t)
            val w = w/2 - 0.5
            fun atStart() = t*d < w+1
            fun atEnd() = T*d < w+1
            for(x in (b.x-w).roundToInt()..(b.x+w).roundToInt()){
                for(z in (b.z-w).roundToInt()..(b.z+w).roundToInt()){
                    val pt = Pt(x.toDouble(),z.toDouble())
                    if(pt.filter(atStart(),atEnd())) points.addPoint(x,z)
                }
            }
            t += step
            T = 1-t
        }
        return points
    }
}