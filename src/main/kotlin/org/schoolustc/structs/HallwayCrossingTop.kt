package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.MyStructWithConfig
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class HallwayCrossingTop(
    config: StructGenConfig,
    val length:Int,
    val door:Int,
):MyStructWithConfig(Companion, Point(5,5,length),config) {
    companion object : MyStructInfo<HallwayCrossingTop>("hallway_crossing_top"){
        override val defaultDirection = Direction2D.ZPlus
        override fun loadTag(tag: CompoundTag) = HallwayCrossingTop(
            tag.read("C"),
            tag.read("l"),
            tag.read("d")
        )

        override fun HallwayCrossingTop.saveTag(tag: CompoundTag) {
            tag.write("C",config)
            tag.write("l",length)
            tag.write("d",door)
        }
    }
    override fun StructBuildScopeWithConfig.build() {
        val wall = SMOOTH_STONE_SLAB
        val floor = SMOOTH_STONE
        val light = SEA_LANTERN
        inRelativeView {
            val zRange = 0..<length
            wall fill Area(0.range,1.range,zRange)
            wall fill Area(4.range,1.range,zRange)
            AIR fill Area(0.range,1.range,door-1..door+1)
            AIR fill Area(4.range,1.range,door-1..door+1)

            floor fill Area(0..4,0.range,zRange)
            for(i in 2..length-3 step 4){
                light fill Point(2,0,i)
            }
        }
    }
}