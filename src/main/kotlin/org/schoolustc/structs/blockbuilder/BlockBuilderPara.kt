package org.schoolustc.structs.blockbuilder

import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.structure.StructureBuildScope

class BlockBuilderPara(
    val area: Area2D,
    val nextToWalls:List<Direction2D>,
    val nextToSplitter:List<Direction2D>,
    val scope:StructureBuildScope
)