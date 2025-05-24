package org.schoolustc.structs.blockbuilder

import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structs.HugeGoldOre
import org.schoolustc.structs.listbuilder.ClassroomBuilder
import org.schoolustc.structureDsl.from
import org.schoolustc.structureDsl.nextBool
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.structure.StructureBuildScope
import org.schoolustc.structureDsl.withChance
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
            ClassroomBuilder.Colors(
                LIGHT_BLUE_CONCRETE, WHITE_CONCRETE, LIGHT_BLUE_CONCRETE
            ) to 2,
            ClassroomBuilder.Colors(
                WHITE_CONCRETE, WHITE_CONCRETE, BLUE_CONCRETE
            ) to 2,
            ClassroomBuilder.Colors(
                YELLOW_CONCRETE, WHITE_CONCRETE, YELLOW_CONCRETE
            ) to 2,
            ClassroomBuilder.Colors(
                ORANGE_CONCRETE, YELLOW_CONCRETE, RED_CONCRETE
            ) to 2,
        )
        val classroom = ClassroomBuilder(para.area.padding(2),para.area.avgY.roundToInt(),rand,rand from colorMap).build(this)
        val ore = rand.withChance(0.3) {
            listOf(HugeGoldOre(area.middle { x, z -> y(x, z) - 30 }))
        } ?: listOf()
        return classroom + getLights() + getLeafWalls() + ore
    }
}