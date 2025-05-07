package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class ClassroomTop(config: StructGenConfig):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<ClassroomTop>(
        "classroom_top",
        Point(8,2,12)
    ){
        override val defaultDirection = Direction2D.ZPlus
        override fun ClassroomTop.saveTag(tag: CompoundTag) {
            tag.write("C",config)
        }
        override fun loadTag(tag: CompoundTag) = ClassroomTop(tag.read("C"))
    }

    override fun StructBuildScopeWithConfig.build() {
        val light = SEA_LANTERN
        val floor = SMOOTH_STONE
        val bound = SMOOTH_STONE_SLAB
        inRelativeView {
            floor fill Area(0..<xSize ,0.range,0..<zSize)
            light fill Point(2,0,2)
            light fill Point(2,0, xSize - 3)
            light fill Point(zSize - 3,0,2)
            light fill Point(zSize - 3,0, xSize - 3)
            light fill Area((xSize / 2).let{ it-1..it },0.range,(zSize / 2).let { it-1..it })
            bound fill Point(1,1,zMax)
            bound fill Area(0.range,1.range,zRange.padding(1))
            bound fill Area(xMax.range,1.range,zRange.padding(1))
            bound fill Area(0..<xSize,1.range,0.range)
            bound fill Area(5..xMax,1.range,zMax.range)
        }
    }
}