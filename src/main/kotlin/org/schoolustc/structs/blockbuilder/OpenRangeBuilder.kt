package org.schoolustc.structs.blockbuilder

import net.minecraft.util.RandomSource
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.Area2D.Companion.area2D
import kotlin.math.absoluteValue

class OpenRangeBuilder(val area:Area2D,val rand:RandomSource,val predication:(Direction2D)->Boolean) {
    companion object {
        fun Pair<Direction2D, IntRange>.toOpenArea(area:Area2D) = first.run { area2D(area.bound(this).range,second) }
    }
    fun build() = Direction2D.entries.filter(predication).flatMap { d ->
        val range = area.l(d.left)
        List(5){
            mutableListOf(rand from range.padding(range.length/4)).apply {
                for(i in 1..range.length/12 - 1){
                    val r = rand from range
                    if(
                        r in range.padding(7)
                        && firstOrNull { (r - it).absoluteValue <= 12 } == null
                    ) {
                        add(r)
                    }
                }
            }
        }.maxBy { it.size }.map { d to it-1..it+1 }
    }
}