package org.schoolustc.structs.builder

import org.schoolustc.structs.Gate
import org.schoolustc.structs.Street
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Area2D.Companion.area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.match
import org.schoolustc.structureDsl.struct.scope.StructGenConfig
import org.schoolustc.structureDsl.struct.builder.MyStructBuilder

class GateBuilder(
    private val wallArea: Area2D,
    private val pos:Int,
    val direction:Direction2D,
    val y:Area2D.()->Int
):MyStructBuilder<Gate> {
    init {
        wallArea.checkNotEmpty().length(direction).match(1)
    }
    private val streetWidth = Street.width
    val area = direction.run {
        area2D(wallArea.l,wallArea.w.first + pos - 2..<wallArea.w.first + pos + streetWidth + 2)
    }.checkNotEmpty()
    val wallArea1 = direction.run {
        area2D(wallArea.l,wallArea.w.first..area.w.first)
    }.checkNotEmpty()
    val wallArea2 = direction.run {
        area2D(wallArea.l,area.w.last..wallArea.w.last)
    }.checkNotEmpty()
    private val finalArea = area
        .sliceStart(direction,2)
        .expand(direction,1)
        .expand(direction.reverse,1)
        .expand(direction.rotate,2)
        .expand(direction.rotate.reverse,2)
    override fun build(): Gate {
        finalArea.width(direction) match Gate.xSize
        finalArea.length(direction) match Gate.zSize
        val config = StructGenConfig.byDirection(finalArea,finalArea.y(),direction,Gate)
        return Gate(config)
    }
}