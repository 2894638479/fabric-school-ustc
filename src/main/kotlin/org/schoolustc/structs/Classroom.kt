package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.*
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class Classroom(config: StructGenConfig,val block:Block): MyStructFixedSize(Companion,config){
    companion object : MyStructFixedSizeInfo<Classroom>(
        "classroom",
        Point(8,5,12)
    ) {
        override val defaultDirection = Direction2D.XPlus
        override fun loadTag(tag: CompoundTag): Classroom {
            return Classroom(
                tag.read("C"),
                tag.read("b")
            )
        }
        override fun Classroom.saveTag(tag: CompoundTag) {
            tag.write("C",config)
            tag.write("b",block)
        }
    }
    override fun StructBuildScopeWithConfig.build() {
        val light = SEA_LANTERN
        val floor = SMOOTH_STONE
        inRelativeView {
            block fillWall fixedArea

            AIR fill Area(xMax.range,1..<ySize,2..4)

            val glassX = GLASS_PANE.state.connected(Direction2D.XPlus,Direction2D.XMin)
            val glassZ = GLASS_PANE.state.connected(Direction2D.ZPlus,Direction2D.ZMin)
            val glassY = 2..<ySize
            glassZ fill Area(0.range,glassY,2..4)
            glassZ fill Area(0.range,glassY,7..9)
            glassZ fill Area(xMax.range,glassY,7..9)
            glassX fill Area(3..4,glassY,0.range)
            glassX fill Area(3..4,glassY,zMax.range)

            floor fill Area(1..xSize - 2,0.range,1..zSize - 2)
            light fill Point(2,0,2)
            light fill Point(2,0,zSize - 3)
            light fill Point(xSize - 3,0,2)
            light fill Point(xSize - 3,0,zSize - 3)
            light fill Area((xSize/2).let{it-1..it},0.range,(zSize/2).let { it-1..it })
        }
    }
}

