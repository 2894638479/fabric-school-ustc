package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.StructBuildScope
import org.schoolustc.structureDsl.struct.StructGenConfig
import kotlin.math.max
import kotlin.math.min

class GrassPath(
    val a1: Area2D,
    val a2:Area2D
):MyStruct(Companion, StructGenConfig(getPoint(a1,a2)), getSize(a1, a2)) {
    companion object : MyStructInfo<GrassPath>("grass_path"){
        override val defaultDirection = Direction2D.XPlus
        override fun loadTag(tag: CompoundTag): GrassPath {
            val arr = tag.getIntArray("ar")
            arr.size.match(8)
            return GrassPath(
                Area2D(arr[0]..arr[1],arr[2]..arr[3]),
                Area2D(arr[4]..arr[5],arr[6]..arr[7])
            )
        }
        override fun GrassPath.saveTag(tag: CompoundTag) {
            tag.putIntArray("ar", listOf(a1.x1,a1.x2,a1.z1,a1.z2,a2.x1,a2.x2,a2.z1,a2.z2))
        }
        private fun getPoint(a1: Area2D,a2: Area2D):Point{
            return Point(
                min(a1.x1,a2.x1),
                0,
                min(a1.z1,a2.z2),
            )
        }
        private fun getSize(a1: Area2D,a2: Area2D):Point{
            val p1 = getPoint(a1, a2)
            val p2 = Point(
                max(a1.x1,a2.x1),
                0,
                max(a1.z1,a2.z2),
            )
            return Point(p2.x - p1.x + 1,1,p2.z - p1.z + 1)
        }
    }

    override fun StructBuildScope.build() {

    }
}