package org.schoolustc.structs.blockbuilder

import org.schoolustc.structureDsl.structure.StructureBuildScope

class NormalBlock(para:BlockBuilderPara): BlockBuilder(para) {
    override fun StructureBuildScope.build() = getLeafWalls() + getLights()
}