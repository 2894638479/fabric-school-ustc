package org.schoolustc.structure

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structurePieces.ClassroomPiece
import org.schoolustc.tools.*

object Classroom: MyStructInfo<ClassroomPiece>(
    "classroom",
    Area(0..7,0..11,0..5)
) {
    override fun loadTag(tag: CompoundTag): ClassroomPiece {
        return ClassroomPiece(tag.getConfig())
    }
    override fun ClassroomPiece.saveTag(tag: CompoundTag) {
        tag.putConfig(config)
    }
    override fun StructBuilder.build() {
        val sel = selector(mapOf(
            DIAMOND_BLOCK to 0.5f,
            GOLD_BLOCK to 1f,
            REDSTONE_BLOCK to 0.1f
        ))
        sel fill area
    }
}
