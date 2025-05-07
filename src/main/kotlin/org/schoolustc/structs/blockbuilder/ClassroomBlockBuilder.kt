package org.schoolustc.structs.blockbuilder

import org.schoolustc.structs.listbuilder.ClassroomBuilder
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.structure.StructureBuildScope
import kotlin.math.roundToInt

class ClassroomBlockBuilder(para: BlockBuilderPara):BlockBuilder(para) {
    override fun StructureBuildScope.build(): List<MyStruct> {
        return ClassroomBuilder(para.area,para.area.avgY.roundToInt(),rand).build(this)
    }
}