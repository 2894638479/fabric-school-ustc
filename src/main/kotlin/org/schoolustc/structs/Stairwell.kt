package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class Stairwell(config: StructGenConfig):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<Stairwell>(
        "stairwell",
        Point(11,5,7)
    ){
        override val defaultDirection = Direction2D.ZMin
        override fun Stairwell.saveTag(tag: CompoundTag) {
            tag.write("C",config)
        }
        override fun loadTag(tag: CompoundTag) = Stairwell(tag.read("C"))
    }

    override fun StructBuildScopeWithConfig.build() {
        inRelativeView {
            RED_CONCRETE fillWall fixedArea
            AIR fill Area(7..9,1..4,0.range)
            AIR fill Area(7..9,1..4,zMax.range)
            val glassX = GLASS_PANE.state.connected(Direction2D.XPlus,Direction2D.XMin)
            val glassZ = GLASS_PANE.state.connected(Direction2D.ZPlus,Direction2D.ZMin)
            val glassY = 2..4
            glassX fill Area(2..4,glassY,0.range)
            glassX fill Area(2..4,glassY,zMax.range)
            glassZ fill Area(xMax.range,glassY,2..4)
            glassZ fill Area(0.range,glassY,2..4)

            "stairwell_inner" putA Point(1,0,1)
            RED_CONCRETE fill Area(7..9,0.range,zRange.padding(1))
            SEA_LANTERN fill Point(8,0,3)
        }
    }
}