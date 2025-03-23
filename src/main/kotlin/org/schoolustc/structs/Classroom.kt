package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.*
import org.schoolustc.structureDsl.structure.MyStructFixedAreaInfo

class Classroom(config: StructGenConfig): MyStructFixedSize(Companion,config){
    companion object : MyStructFixedAreaInfo<Classroom>(
        "classroom",
        Point(8,5,12)
    ) {
        override val defaultDirection = Direction2D.X1
        override fun loadTag(tag: CompoundTag): Classroom {
            return Classroom(tag.getConfig())
        }
        override fun Classroom.saveTag(tag: CompoundTag) {
            tag.putConfig(config)
        }
    }
    override fun StructBuildScope.build() {
        RED_TERRACOTTA fillWall Area(0..7,1..4,0..11)
        SMOOTH_STONE fill Area(0..7,0..0,0..11)
        val light = SEA_LANTERN
        light fill Point(2,0,2)
        light fill Point(5,0,2)
        light fill Point(2,0,9)
        light fill Point(5,0,9)
        light fill Area(3..4,0..0,5..6)
        AIR fill Area(7..7,1..3,2..4)
        val windowY = 2..4
        val windowBlock = GLASS_PANE
        windowBlock fillX Area(3..4,windowY,0..0)
        windowBlock fillX Area(3..4,windowY,11..11)
        windowBlock fillZ Area(0..0,windowY,2..4)
        windowBlock fillZ Area(0..0,windowY,7..9)
        windowBlock fillZ Area(7..7,windowY,7..9)
        "test" put Point(1,1,1)
    }
}

