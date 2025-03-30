package org.schoolustc.structureDsl

fun interface Selector <T> {
    fun select(): T
}