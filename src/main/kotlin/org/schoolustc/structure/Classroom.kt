package org.schoolustc.structure

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.schoolustc.structurePieces.ClassroomPiece
import org.schoolustc.tools.*

object Classroom: MyStructInfo<ClassroomPiece>(
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
        GLASS_PANE fillConnectable Area(3..4,windowY,0..0)
    }
}
