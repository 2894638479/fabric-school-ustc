package org.schoolustc.structureDsl

import net.minecraft.util.RandomSource

fun interface Selector <T> {
    fun select(): T
    companion object {
        fun <T> selector(rand:RandomSource,map:Map<T,Float>): Selector<T> {
            val sum = map.values.sum()
            return Selector {
                val r = rand.nextFloat() * sum
                var f = 0f
                for((block,weight) in map){
                    f += weight
                    if(r < f) return@Selector block
                }
                return@Selector map.keys.last()
            }
        }

    }
}