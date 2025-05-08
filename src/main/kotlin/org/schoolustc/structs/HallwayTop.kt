package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.MyStructWithConfig
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class HallwayTop(config: StructGenConfig, val length:Int,val block: Block):MyStructWithConfig(Companion, Point(5,2,length),config) {
    companion object : MyStructInfo<HallwayTop>("hallway_top"){
        override val defaultDirection = Direction2D.ZPlus
        override fun loadTag(tag: CompoundTag) = HallwayTop(
            tag.read("C"),
            tag.read("l"),
            tag.read("b")
        )

        override fun HallwayTop.saveTag(tag: CompoundTag) {
            tag.write("C",config)
            tag.write("l",length)
            tag.write("b",block)
        }
    }

    override fun StructBuildScopeWithConfig.build() {
        val light = SEA_LANTERN
        inRelativeView {
            val zRange = 0..<length
            SMOOTH_STONE fill Area(1..3,0.range,zRange)
            block fill Area(0.range,0.range,zRange)
            block fill Area(4.range,0.range,zRange)
            for(i in 1..length-3 step 4){
                light fill Point(2,0,i)
            }
            SMOOTH_STONE_SLAB fill Area(0.range,1.range,zRange)
            SMOOTH_STONE_SLAB fill Area(4.range,1.range,zRange)
        }
    }
}