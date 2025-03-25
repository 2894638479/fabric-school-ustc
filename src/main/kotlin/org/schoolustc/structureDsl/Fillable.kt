package org.schoolustc.structureDsl

interface Fillable {
    fun fill(block:(Point)->Unit)
}