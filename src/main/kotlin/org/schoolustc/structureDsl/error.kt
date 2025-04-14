package org.schoolustc.structureDsl

infix fun Int.match(other:Int):Int {
    if (this != other) error("int not match: $this and $other")
    return this
}

fun <T> T.match(condition:(T)->Boolean):T {
    if (!condition(this)) error("not match condition")
    return this
}