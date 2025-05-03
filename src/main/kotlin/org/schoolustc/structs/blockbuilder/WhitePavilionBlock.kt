package org.schoolustc.structs.blockbuilder

import org.schoolustc.structs.WhitePavilion
import org.schoolustc.structureDsl.Area2D.Companion.area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.from
import org.schoolustc.structureDsl.middle
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.scope.StructGenConfig
import org.schoolustc.structureDsl.structure.StructureBuildScope
import kotlin.math.roundToInt

class WhitePavilionBlock(para: BlockBuilderPara):BlockBuilder(para) {
    override fun StructureBuildScope.build(): List<MyStruct> {
        val direction = rand from Direction2D.entries
        val area = direction.run {
            WhitePavilion.run {
                val midL = area.l.middle.roundToInt()
                val midW = area.w.middle.roundToInt()
                val l = midL - xSize/2
                val w = midW - zSize/2
                area2D(l..<l+xSize,w..<w+zSize)
            }
        }
        val midX = area.x.middle.roundToInt()
        val midZ = area.z.middle.roundToInt()
        val y = y(midX,midZ)
        val config = StructGenConfig.byDirection(area,y,direction,WhitePavilion)
        return listOf(WhitePavilion(config))
    }
}