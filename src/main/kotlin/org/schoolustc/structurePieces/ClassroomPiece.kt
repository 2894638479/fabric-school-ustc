package org.schoolustc.structurePieces

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*

class ClassroomPiece(config: StructGenConfig):MyStruct(Companion,config){
    companion object : MyStructInfo<ClassroomPiece>(
        "classroom",
        Area(0..7,0..4,0..11)
    ) {
        override fun loadTag(tag: CompoundTag): ClassroomPiece {
            return ClassroomPiece(tag.getConfig())
        }
        override fun ClassroomPiece.saveTag(tag: CompoundTag) {
            tag.putConfig(config)
        }
        override fun StructBuilder.build() {
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
            windowBlock fillC Area(3..4,windowY,0..0)
            windowBlock fillC Area(3..4,windowY,11..11)
            windowBlock fillC Area(0..0,windowY,2..4)
            windowBlock fillC Area(0..0,windowY,7..9)
            windowBlock fillC Area(7..7,windowY,7..9)
            "test" put Point(1,1,1)
        }
    }
}

