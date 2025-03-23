package org.schoolustc.structureDsl

enum class Direction {
    X1, X2, Y1, Y2, Z1, Z2;
    val isX get() = this == X1 || this == X2
    val isY get() = this == Y1 || this == Y2
    val isZ get() = this == Z1 || this == Z2
}