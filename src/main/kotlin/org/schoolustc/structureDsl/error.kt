package org.schoolustc.structureDsl

infix fun Int.match(other:Int):Int {
    if (this != other) error("int not match: $this and $other")
    return this
}

fun Int.match(condition:(Int)->Boolean):Int {
    if (!condition(this)) error("int not match condition")
    return this
}