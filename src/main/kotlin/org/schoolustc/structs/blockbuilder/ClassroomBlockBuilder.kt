package org.schoolustc.structs.blockbuilder

import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structs.listbuilder.ClassroomBuilder
import org.schoolustc.structureDsl.from
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.structure.StructureBuildScope
import kotlin.math.roundToInt

class ClassroomBlockBuilder(para: BlockBuilderPara):BlockBuilder(para) {
    override fun StructureBuildScope.build(): List<MyStruct> {
        val colorMap = mapOf(
            ClassroomBuilder.Colors(
                RED_CONCRETE, RED_CONCRETE, RED_CONCRETE
            ) to 2,
            ClassroomBuilder.Colors(
                WHITE_CONCRETE, RED_CONCRETE, WHITE_CONCRETE
            ) to 2,
            ClassroomBuilder.Colors(
                RED_CONCRETE, WHITE_CONCRETE, RED_CONCRETE
            ) to 2,
            ClassroomBuilder.Colors(
                LIGHT_BLUE_CONCRETE, BLUE_CONCRETE, WHITE_CONCRETE
            ) to 2,
        )
        return ClassroomBuilder(para.area,para.area.avgY.roundToInt(),rand,rand from colorMap).build(this)
    }
}