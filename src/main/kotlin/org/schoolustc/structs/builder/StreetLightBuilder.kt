package org.schoolustc.structs.builder

import com.google.common.math.IntMath.pow
import org.schoolustc.structs.StreetLight
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structureDsl.struct.builder.MyStructBuilder
import kotlin.math.sqrt

class StreetLightBuilder(
    val x:Int,val z:Int,
    val direction:Direction2D
):MyStructBuilder<StreetLight> {
    override fun build(): StreetLight {
        val area = Area2D(x..x,z..z).expand(direction,1)
        val config = StructGenConfig.byDirection(area,0,direction,StreetLight)
        return StreetLight(config)
    }
    fun distanceTo(other:StreetLightBuilder): Double = sqrt((pow(x-other.x,2)+ pow(z-other.z,2)).toDouble())
}