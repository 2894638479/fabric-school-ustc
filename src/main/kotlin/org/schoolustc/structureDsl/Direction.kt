package org.schoolustc.structureDsl

enum class Direction {
    XPlus, XMin, YPlus, YMin, ZPlus, ZMin;
    val isX get() = this == XPlus || this == XMin
    val isY get() = this == YPlus || this == YMin
    val isZ get() = this == ZPlus || this == ZMin
}