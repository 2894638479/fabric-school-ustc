package org.schoolustc.structureDsl

fun interface Fillable {
    fun fill(block:(Point)->Unit)
}