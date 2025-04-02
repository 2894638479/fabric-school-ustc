package org.schoolustc.structs.blockBuilder

import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D

class BlockBuilderPara(
    val area: Area2D,
    val nextToWalls:List<Direction2D>,
    val nextToSplitter:List<Direction2D>
)