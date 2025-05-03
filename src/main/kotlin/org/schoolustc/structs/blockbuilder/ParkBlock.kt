package org.schoolustc.structs.blockbuilder

import org.schoolustc.structs.Park
import org.schoolustc.structureDsl.structure.StructureBuildScope

class ParkBlock(para: BlockBuilderPara):BlockBuilder(para){
    override fun StructureBuildScope.build() = listOf(Park(rand.nextLong(),area))
}